package nl.tudelft.contextproject.camera;

import java.util.Observer;

/**
 * Abstract class to represent a connection with a camera. A class
 * that extends this class should observe a Camera-object and send 
 * its operations to the actual camera.
 * 
 * @since 0.4
 */
public abstract class CameraConnection implements Observer {
    
    /**
     * Sets up the connection to the camera.
     * @return true iff the connection was set up successfully.
     */
    public abstract boolean setUpConnection();
    
    /**
     * Returns true iff there is an active connection to the camera.
     * @return true iff the camera is connected.
     */
    public abstract boolean isConnected();
    
    /**
     * Returns the current camera settings of the camera as a
     * CameraSettings object.
     * @return the current camera settings of the camera.
     */
    public abstract CameraSettings getCurrentCameraSettings();
    
    /**
     * Returns the current pan and tilt values of the camera in
     * the form of an array.
     * @return an array with the pan and tilt values. [0] holds
     *      the pan value, [1] holds the tilt value.
     */
    public abstract int[] getCurrentPanTilt();
    
    /**
     * Returns the current zoom value of the camera.
     * @return the current zoom value of the camera.
     */
    public abstract int getCurrentZoom();
    
    /**
     * Returns the current focus value of the camera.
     * In the case of a LiveCameraConnection, this method may
     * return -1 when auto focus is on.
     * @return the current focus value of the camera.
     */
    public abstract int getCurrentFocus();
    
    /**
     * Pans and tilts to an absolute position.
     * @param panValue the absolute pan value to pan to.
     * @param tiltValue the absolute tilt value to tilt to.
     * @return true iff the operation was performed successfully.
     */
    protected abstract boolean absPanTilt(int panValue, int tiltValue);
    
    /**
     * Pans to an absolute position.
     * @param value the absolute value to pan to.
     * @return true iff the operation was performed successfully.
     */
    protected abstract boolean absPan(int value);
    
    /**
     * Tilts to an absolute position.
     * @param value the absolute value to tilt to.
     * @return true iff the operation was performed successfully.
     */
    protected abstract boolean absTilt(int value);
    
    /**
     * Sets the zoom level to an absolute position.
     * @param value the absolute value to zoom to.
     * @return true iff the operation was performed successfully.
     */
    protected abstract boolean absZoom(int value);
    
    /**
     * Sets the focus to an absolute position.
     * @param value the absolute value to focus to.
     * @return true iff the operation was performed successfully.
     */
    protected abstract boolean absFocus(int value);
    
    /**
     * Pans and tilts the camera a certain offset.
     * @param panOffset offset to pan.
     * @param tiltOffset offset to tilt.
     * @return true iff the operation was performed successfully.
     */
    protected abstract boolean relPanTilt(int panOffset, int tiltOffset);
    
    /**
     * Pans the camera a certain offset.
     * @param offset offset to pan.
     * @return true iff the operation was performed successfully.
     */
    protected abstract boolean relPan(int offset);
    
    /**
     * Tilts the camera a certain offset.
     * @param offset offset to tilt.
     * @return true iff the operation was performed successfully.
     */
    protected abstract boolean relTilt(int offset);
    
    /**
     * Zooms the camera a certain offset.
     * @param offset offset to zoom.
     * @return true iff the operation was performed successfully.
     */
    protected abstract boolean relZoom(int offset);
    
    /**
     * Changes the focus of the camera a certain offset.
     * @param offset offset to change the focus.
     * @return true iff the operation was performed successfully.
     */
    protected abstract boolean relFocus(int offset);
}
