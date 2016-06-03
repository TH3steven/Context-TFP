package nl.tudelft.contextproject.camera;

/**
 * Class to represent camera settings. Can be extended with color
 * settings etc.
 * 
 * @since 0.2
 */
public class CameraSettings {

    private int pan;
    private int tilt;
    private int zoom;
    private int focus;

    /**
     * Constructs a camera settings object with orientation,
     * zoom level and focus level initialized to 0.
     */
    public CameraSettings() {
        pan = 0;
        tilt = 0;
        zoom = 0;
        focus = 0;
    }

    /**
     * Constructs a camera settings object with a certain orientation, 
     * zoom level and focus.
     * 
     * @param panPos Horizontal orientation level.
     * @param tiltPos Vertical orientation level.
     * @param zoomPos Zoom level.
     * @param focusPos Focus level.
     */
    public CameraSettings(int panPos, int tiltPos, int zoomPos, int focusPos) {
        pan = panPos;
        tilt = tiltPos;
        zoom = zoomPos;
        focus = focusPos;
    }

    /**
     * Gets the pan level (horizontal orientation).
     * @return Pan level (horizontal orientation).
     */
    public int getPan() {
        return pan;
    }

    /**
     * Sets the pan level of the camera settings to a specified value.
     * @param panPos the zoom value to set the camera to.
     */
    public void setPan(int panPos) {
        this.pan = panPos;
    }

    /**
     * Sets the tilt level of the camera settings to a specified value.
     * @param tiltPos the tilt value to set the camera to.
     */
    public void setTilt(int tiltPos) {
        this.tilt = tiltPos;
    }

    /**
     * Sets the zoom level of the camera settings to a specified value.
     * @param zoomPos the zoom value to set the camera to.
     */
    public void setZoom(int zoomPos) {
        this.zoom = zoomPos;
    }

    /**
     * Sets the focus level of the camera settings to a specified value.
     * @param focusPos the focus value to set the camera to.
     */
    public void setFocus(int focusPos) {
        this.focus = focusPos;
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
     * Gets the focus level.
     * @return focus level.
     */
    public int getFocus() {
        return focus; 
    }

    /**
     * Pans the camera settings a certain offset.
     * @param offset The offset to pan the camera.
     */
    protected void pan(int offset) {
        pan += offset;
    }

    /**
     * Tilts the camera settings a certain offset.
     * @param offset The offset to tilt the camera.
     */
    protected void tilt(int offset) {
        tilt += offset;
    }

    /**
     * Zooms the camera settings a certain offset.
     * @param offset The offset to zoom the camera.
     */
    protected void zoom(int offset) {
        zoom += offset;
    }

    /**
     * Focuses the camera settings to a certain offset.
     * @param offset The offset to focus the camera.
     */
    protected void focus(int offset) {
        focus += offset;
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
        if (obj instanceof CameraSettings) {
            CameraSettings other = (CameraSettings) obj;

            return focus == other.focus
                    && pan == other.pan
                    && tilt == other.tilt
                    && zoom == other.zoom;
        }

        return false;
    }
}
