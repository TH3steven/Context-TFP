package nl.tudelft.contextproject.gui;

import com.sun.jna.Memory;

import javafx.application.Platform;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;
import uk.co.caprica.vlcj.component.DirectMediaPlayerComponent;
import uk.co.caprica.vlcj.player.direct.BufferFormat;
import uk.co.caprica.vlcj.player.direct.DirectMediaPlayer;
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
    private String streamLink;
    private ImageView imageView;
    private FloatProperty videoSourceRatioProperty;
    
    /**
     * Creates a LiveStreamHandler object.
     */
    public LiveStreamHandler() {
        videoSourceRatioProperty = new SimpleFloatProperty(0.4f);
    }
    
    /**
     * Starts playing the media.
     */
    public void start() {
        mediaPlayer.getMediaPlayer().playMedia(streamLink);
    }
    
    /**
     * Stops playing the media and release associated resources.
     */
    public void stop() {
        mediaPlayer.getMediaPlayer().stop();
        mediaPlayer.getMediaPlayer().release();
    }
    
    /**
     * Returns an ImageView object that can be put into the JavaFX framework.
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
            return new RV32BufferFormat((int) width, (int) height);
        }) {
            @Override
            public void display(DirectMediaPlayer mediaPlayer, Memory[] nativeBuffers, BufferFormat bufferFormat) {
                Platform.runLater(() -> {
                    Memory nativeBuffer = mediaPlayer.lock()[0];
                    try {
                        ByteBuffer byteBuffer = nativeBuffer.getByteBuffer(0, nativeBuffer.size());
                        pixelWriter.setPixels(0, 0, bufferFormat.getWidth(), bufferFormat.getHeight(), 
                                pixelFormat, byteBuffer, bufferFormat.getPitches()[0]);
                    } finally {
                        mediaPlayer.unlock();
                    }
                });
            }
        };
        return imageView;
    }
    
    public FloatProperty getRatio() {
        return videoSourceRatioProperty;
    }
}
