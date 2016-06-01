package nl.tudelft.contextproject.gui;

import com.sun.jna.Memory;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
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
    
    /**
     * Creates a LiveStreamHandler object.
     */
    public LiveStreamHandler() {
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
     * @return a Canvas object that can be put into the JavaFX framework.
     */
    public Canvas createCanvas(String streamLink) {
        Canvas canvas = new Canvas();
        this.streamLink = streamLink;
        this.pixelWriter = canvas.getGraphicsContext2D().getPixelWriter();
        this.pixelFormat = PixelFormat.getByteBgraPreInstance();
        this.mediaPlayer = new DirectMediaPlayerComponent((sourceWidth, sourceHeight) -> {
            Platform.runLater( () -> {
                canvas.setWidth(sourceWidth);
                canvas.setHeight(sourceHeight);
            });
            return new RV32BufferFormat(sourceWidth, sourceHeight);
        });
        return canvas;
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
