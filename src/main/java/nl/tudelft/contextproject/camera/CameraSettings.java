package nl.tudelft.contextproject.camera;

/**
 * Class to represent camera settings. Can be extended with colour
 * settings etc.
 * @since 0.2
 */
public class CameraSettings {
    public static final int PAN_LIMIT_LOW = 0;
    public static final int PAN_LIMIT_HIGH = 65536;
    
    public static final int TILT_LIMIT_LOW = 0;
    public static final int TILT_LIMIT_HIGH = 65536;
    
    public static final int ZOOM_LIMIT_LOW = 0;
    public static final int ZOOM_LIMIT_HIGH = 100;

    public static final int FOCUS_LIMIT_LOW = 1365;
    public static final int FOCUS_LIMIT_HIGH = 4095;
    
    private int pan;
    private int tilt;
    private int zoom;
    private int focus;
    
    /**
     * Constructs a camera settings object with orientation and
     * zoom level at base levels.
     */
    public CameraSettings() {
        pan = PAN_LIMIT_LOW;
        tilt = TILT_LIMIT_LOW;
        zoom = ZOOM_LIMIT_LOW;
        focus = FOCUS_LIMIT_LOW;
    }
    
    /**
     * Constructs a camera settings object with a certain orientation
     * and zoom level.
     * @param panPos Horizontal orientation level.
     * @param tiltPos Vertical orientation level.
     * @param zoomPos Zoom level.
     */
    public CameraSettings(int panPos, int tiltPos, int zoomPos, int focusPos) {
        pan =   (panPos < PAN_LIMIT_LOW) ? PAN_LIMIT_LOW :
                (panPos > PAN_LIMIT_HIGH) ? PAN_LIMIT_HIGH : panPos;

        tilt =  (tiltPos < TILT_LIMIT_LOW) ? TILT_LIMIT_LOW :
                (tiltPos > TILT_LIMIT_HIGH) ? TILT_LIMIT_HIGH : tiltPos;

        zoom =  (zoomPos < ZOOM_LIMIT_LOW) ? ZOOM_LIMIT_LOW :
                (zoomPos > ZOOM_LIMIT_HIGH) ? ZOOM_LIMIT_HIGH : zoomPos;

        focus = (focusPos < FOCUS_LIMIT_LOW) ? FOCUS_LIMIT_LOW :
                (focusPos > FOCUS_LIMIT_HIGH) ? FOCUS_LIMIT_HIGH : focusPos;
    }
    
    /**
     * Gets the pan level (horizontal orientation).
     * @return Pan level (horizontal orientation).
     */
    public int getPan() {
        return pan;
    }

    /**
     * Sets the pan level of the camera to a specified value without
     * exceeding its lowest or highest zoom levels.
     *
     * @param panPos the zoom value to set the camera to.
     */
    public void setPan(int panPos) {
        this.pan =   (panPos < PAN_LIMIT_LOW) ? PAN_LIMIT_LOW :
                (panPos > PAN_LIMIT_HIGH) ? PAN_LIMIT_HIGH : panPos;
    }

    /**
     * Sets the tilt level of the camera to a specified value without
     * exceeding its lowest or highest tilt levels.
     *
     * @param tiltPos the tilt value to set the camera to.
     */
    public void setTilt(int tiltPos) {
        this.tilt =  (tiltPos < TILT_LIMIT_LOW) ? TILT_LIMIT_LOW :
                (tiltPos > TILT_LIMIT_HIGH) ? TILT_LIMIT_HIGH : tiltPos;
    }

    /**
     * Sets the zoom level of the camera to a specified value without
     * exceeding its lowest or highest zoom levels.
     *
     * @param zoomPos the zoom value to set the camera to.
     */
    public void setZoom(int zoomPos) {
        this.zoom =  (zoomPos < ZOOM_LIMIT_LOW) ? ZOOM_LIMIT_LOW :
                (zoomPos > ZOOM_LIMIT_HIGH) ? ZOOM_LIMIT_HIGH : zoomPos;
    }

    /**
     * Sets the focus level of the camera to a specified value without
     * exceeding its lowest or highest focus levels.
     * 
     * @param focusPos the focus value to set the camera to.
     */
    public void setFocus(int focusPos) {
        this.focus = (focusPos < FOCUS_LIMIT_LOW) ? FOCUS_LIMIT_LOW :
                (focusPos > FOCUS_LIMIT_HIGH) ? FOCUS_LIMIT_HIGH : focusPos;
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
     * Gets the Focus Level of the camera.
     * @return focus level.
     */
    public int getFocus() {
        return focus; 
    }

    /**
     * Pans the camera a certain offset. It cannot pan past
     * the pan limits, but it does try to pan as far as possible.
     * 
     * @param offset The offset to pan the camera.
     */
    protected void pan(int offset) {
        pan = (pan + offset < PAN_LIMIT_LOW) ? PAN_LIMIT_LOW : 
            (pan + offset > PAN_LIMIT_HIGH) ? PAN_LIMIT_HIGH : pan + offset;
    }
    
    /**
     * Tilts the camera a certain offset. It cannot tilt past
     * the tilt limits, but it does try to tilt as far as possible.
     * 
     * @param offset The offset to tilt the camera.
     */
    protected void tilt(int offset) {
        tilt = (tilt + offset < TILT_LIMIT_LOW) ? TILT_LIMIT_LOW : 
               (tilt + offset > TILT_LIMIT_HIGH) ? TILT_LIMIT_HIGH : tilt + offset;
    }
    
    /**
     * Zooms the camera a certain offset. It cannot zoom past
     * the zoom limits, but it does try to zoom as far as possible.
     * 
     * @param offset The offset to zoom the camera.
     */
    protected void zoom(int offset) {
        zoom = (zoom + offset < ZOOM_LIMIT_LOW) ? ZOOM_LIMIT_LOW : 
               (zoom + offset > ZOOM_LIMIT_HIGH) ? ZOOM_LIMIT_HIGH : zoom + offset;
    }

    /**
     * Focuses the camera to a certain offset. It cannot focus past
     * the focus limit, but it tries to focus as far as possible.
     *
     * @param offset The offset to focus the camera.
     */
    protected void focus(int offset) {
        focus = (focus + offset < FOCUS_LIMIT_LOW) ? FOCUS_LIMIT_LOW :
                (focus + offset > FOCUS_LIMIT_HIGH) ? FOCUS_LIMIT_HIGH : focus + offset;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + focus;
        result = prime * result + pan;
        result = prime * result + tilt;
        result = prime * result + zoom;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof CameraSettings)) {
            return false;
        }
        CameraSettings other = (CameraSettings) obj;
        boolean result = focus == other.focus
                && pan == other.pan
                && tilt == other.tilt
                && zoom == other.zoom;
        return result;
    }
}
