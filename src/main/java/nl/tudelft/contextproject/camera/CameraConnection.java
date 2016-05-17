package main.java.nl.tudelft.contextproject.camera;

import java.util.Observer;

/**
 * Abstract class to represent a connection with a camera. A class
 * that extends this class should observe a Camera-object and send 
 * its operations to the actual camera.
 * 
 * @since 0.4
 */
public abstract class CameraConnection implements Observer {
    
    public abstract boolean setUpConnection();
    
    public abstract boolean isConnected();
    
    public abstract CameraSettings getCurrentCameraSettings();
    
    protected abstract boolean absPanTilt(int panValue, int tiltValue);
    
    protected abstract boolean absPan(int value);
    
    protected abstract boolean absTilt(int value);
    
    protected abstract boolean absZoom(int value);
    
    protected abstract boolean absFocus(int value);
    
    protected abstract boolean relPan(int offset);
    
    protected abstract boolean relTilt(int offset);
    
    protected abstract boolean relZoom(int offset);
    
    protected abstract boolean relFocus(int offset);
}
