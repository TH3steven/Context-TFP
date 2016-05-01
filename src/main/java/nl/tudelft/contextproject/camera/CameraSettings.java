package main.java.nl.tudelft.contextproject.camera;

/**
 * Class to represent camera settings. Can be extended with colour
 * settings etc.
 * 
 * @author Bart van Oort
 * @since 0.2
 */
public class CameraSettings {
    public static final int PAN_LIMIT_LOW = 0;
    public static final int PAN_LIMIT_HIGH = 65536;
    
    public static final int TILT_LIMIT_LOW = 0;
    public static final int TILT_LIMIT_HIGH = 65536;
    
    public static final int ZOOM_LIMIT_LOW = 0;
    public static final int ZOOM_LIMIT_HIGH = 100;
    
    private int pan;
    private int tilt;
    private int zoom;
    
    /**
     * Constructs a camera settings object with orientation and
     * zoom level at base levels.
     */
    public CameraSettings() {
        pan = PAN_LIMIT_LOW;
        tilt = TILT_LIMIT_LOW;
        zoom = ZOOM_LIMIT_LOW;
    }
    
    /**
     * Constructs a camera settings object with a certain orientation
     * and zoom level.
     * @param panPos Horizontal orientation level.
     * @param tiltPos Vertical orientation level.
     * @param zoomPos Zoom level.
     */
    public CameraSettings(int panPos, int tiltPos, int zoomPos) {
        pan = (panPos < PAN_LIMIT_LOW) ? PAN_LIMIT_LOW : 
            (panPos > PAN_LIMIT_HIGH) ? PAN_LIMIT_HIGH : panPos;
        tilt = (tiltPos < TILT_LIMIT_LOW) ? TILT_LIMIT_LOW : 
            (tiltPos > TILT_LIMIT_HIGH) ? TILT_LIMIT_HIGH : tiltPos;
        zoom = (zoomPos < ZOOM_LIMIT_LOW) ? ZOOM_LIMIT_LOW : 
            (zoomPos > ZOOM_LIMIT_HIGH) ? ZOOM_LIMIT_HIGH : zoomPos;
    }
    
    /**
     * Gets the pan level (horizontal orientation).
     * @return Pan level (horizontal orientation).
     */
    public int getPan() {
        return pan;
    }
    
    /**
     * Gets the tilt level (vertical orientation).
     * @return Tilt level (vertical orientation).
     */
    public int getTilt() {
        return tilt;
    }
    
    /**
     * Gets the zoom level.
     * @return Zoom level.
     */
    public int getZoom() {
        return zoom;
    }
    
    /**
     * Pans the camera a certain offset. It cannot pan past
     * the pan limits, but it does try to pan as far as possible.
     * 
     * @param offset The offset to pan the camera.
     */
    public void pan(int offset) {
        pan = (pan + offset < PAN_LIMIT_LOW) ? PAN_LIMIT_LOW : 
            (pan + offset > PAN_LIMIT_HIGH) ? PAN_LIMIT_HIGH : pan + offset;
    }
    
    /**
     * Tilts the camera a certain offset. It cannot tilt past
     * the tilt limits, but it does try to tilt as far as possible.
     * 
     * @param offset The offset to tilt the camera.
     */
    public void tilt(int offset) {
        tilt = (tilt + offset < TILT_LIMIT_LOW) ? TILT_LIMIT_LOW : 
            (tilt + offset > TILT_LIMIT_HIGH) ? TILT_LIMIT_HIGH : tilt + offset;
    }
    
    /**
     * Zooms the camera a certain offset. It cannot zoom past
     * the zoom limits, but it does try to zoom as far as possible.
     * 
     * @param offset The offset to zoom the camera.
     */
    public void zoom(int offset) {
        zoom = (zoom + offset < ZOOM_LIMIT_LOW) ? ZOOM_LIMIT_LOW : 
            (zoom + offset > ZOOM_LIMIT_HIGH) ? ZOOM_LIMIT_HIGH : zoom + offset;
    }
}
