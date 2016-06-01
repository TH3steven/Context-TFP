package nl.tudelft.contextproject.gui;

import com.sun.jna.Memory;



import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;
import uk.co.caprica.vlcj.component.DirectMediaPlayerComponent;
import uk.co.caprica.vlcj.player.direct.BufferFormat;
import uk.co.caprica.vlcj.player.direct.DefaultDirectMediaPlayer;
import uk.co.caprica.vlcj.player.direct.format.RV32BufferFormat;



import java.nio.ByteBuffer;

/**
 * Handler for streaming media into the GUI through VLC.
 * 
 * @since 0.5
 */
public class LiveStreamHandler {
    
    private PixelWriter pixelWriter;
    private WritablePixelFormat<ByteBuffer> pixelFormat;
    private DirectMediaPlayerComponent mediaPlayer;
    private final AnimationTimer timer;
    private String streamLink;
    private ImageView imageView;
    private FloatProperty videoSourceRatioProperty;
    
    /**
     * Creates a LiveStreamHandler object.
     */
    public LiveStreamHandler() {
        videoSourceRatioProperty = new SimpleFloatProperty(0.4f);
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                renderFrame();
            }
        };
    }
    
    /**
     * Starts playing the media.
     */
    public void start() {
        mediaPlayer.getMediaPlayer().playMedia(streamLink);
        timer.start();
    }
    
    /**
     * Stops playing the media and release associated resources.
     */
    public void stop() {
        mediaPlayer.getMediaPlayer().stop();
        mediaPlayer.getMediaPlayer().release();
        timer.stop();
    }
    
    /**
     * Returns a Canvas object that can be put into the JavaFX framework.
     * @param streamLink link to media stream.
     * @param width Width of the Canvas.
     * @param height Height of the Canvas.
     * @return a Canvas object displaying the stream.
     */
    public Canvas createCanvas(String streamLink, double width, double height) {
        Canvas canvas = new Canvas();
        this.streamLink = streamLink;
        this.pixelWriter = canvas.getGraphicsContext2D().getPixelWriter();
        this.pixelFormat = PixelFormat.getByteBgraPreInstance();
        this.mediaPlayer = new DirectMediaPlayerComponent((sourceWidth, sourceHeight) -> {
            Platform.runLater( () -> {
                canvas.setWidth(width);
                canvas.setHeight(height);
            });
            return new RV32BufferFormat((int) width, (int) height);
        });
        return canvas;
    }
    
    /**
     * Experimental method which uses an ImageView instead of a Canvas.
     * @param streamLink link to media stream.
     * @param width Width of the ImageView.
     * @param height Height of the ImageView.
     * @return a ImageView object displaying the stream.
     */
    public ImageView createImageView(String streamLink, double width, double height) {
        WritableImage writableImage = new WritableImage((int) width, (int) height);
        imageView = new ImageView(writableImage);
        this.streamLink = streamLink;
        this.pixelWriter = writableImage.getPixelWriter();
        this.pixelFormat = PixelFormat.getByteBgraPreInstance();
        this.mediaPlayer = new DirectMediaPlayerComponent((sourceWidth, sourceHeight) -> {
            Platform.runLater( () -> {
                videoSourceRatioProperty.set((float) sourceHeight / (float) sourceWidth);
            });
            return new RV32BufferFormat(1080, 1920);
        });
        return imageView;        
    }
    
    public FloatProperty getRatio() {
        return videoSourceRatioProperty;
    }
    
    /**
     * Renders a single frame of the video playback.
     */
    private void renderFrame() {
        Memory[] nativeBuffers = mediaPlayer.getMediaPlayer().lock();
        if (nativeBuffers != null) {
            Memory nativeBuffer = nativeBuffers[0];
            if (nativeBuffer != null) {
                ByteBuffer byteBuffer = nativeBuffer.getByteBuffer(0, nativeBuffer.size());
                BufferFormat bufferFormat = ((DefaultDirectMediaPlayer) mediaPlayer.getMediaPlayer()).getBufferFormat();
                if (bufferFormat.getWidth() > 0 && bufferFormat.getHeight() > 0) {
                    pixelWriter.setPixels(0, 0, bufferFormat.getWidth(), bufferFormat.getHeight(), 
                            pixelFormat, byteBuffer, bufferFormat.getPitches()[0]);
                }
            }
        }
        mediaPlayer.getMediaPlayer().unlock();
    }
}
