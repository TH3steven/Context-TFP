package main.java.nl.tudelft.contextproject.camera;

import java.util.Observable;

/**
 * Class to represent a live connection with a camera. It is
 * responsible for the communication between our data model and
 * the actual camera, in this case a Panasonic AW-HE130.
 * 
 * <p>In the {@link #setUpConnection()} method, it will check if the
 * correct {@link #CAMERA_MODEL} is being talked to.
 * 
 * @since 0.4
 */
public class LiveCameraConnection extends CameraConnection {
    
    public static final String CAMERA_MODEL = "AW-HE130";
    
    private String address;
    private boolean connected;
    private boolean autoFocus;
    
    /**
     * Creates a LiveCameraConnection object. Assumes that the
     * address given is the correctly formulated IP address of the
     * camera to connect to.
     * @param address IP address of the camera to connect to.
     */
    public LiveCameraConnection(String address) {
        this.address = address;
        this.connected = false;
    }
    
    @Override
    public boolean setUpConnection() {
        // TODO Auto-generated method stub
        return false;
    }
    
    @Override
    public boolean isConnected() {
        return connected;
    }
    
    /**
     * Returns true iff the camera is on auto focus.
     * @return true iff the camera is on auto focus.
     */
    public boolean hasAutoFocus() {
        return autoFocus;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (!(o instanceof Camera)) {
            return;
        }
        Camera cam = (Camera) o;
        if (arg instanceof CameraSettings) {
            mutateSettings((CameraSettings) arg);
        }
    }
    
    /**
     * Finds the least amount of commands to send to the 
     * camera in order to apply the specified camera settings
     * @param toSet camera settings to apply to the camera.
     * @return true iff the camera was set to the specified settings.
     */
    private boolean mutateSettings(CameraSettings toSet) {
        CameraSettings curSettings = getCurrentCameraSettings();
        boolean result = true;
        if (curSettings.getPan() != toSet.getPan() 
                || curSettings.getTilt() != toSet.getTilt()) {
            result = result && absPanTilt(toSet.getPan(), toSet.getTilt());
        }
        if (curSettings.getZoom() != toSet.getZoom()) {
            result = result && absZoom(toSet.getZoom());
        }
        if (!autoFocus && curSettings.getFocus() != toSet.getFocus()) {
            result = result && absFocus(toSet.getFocus());
        }
        return result;
    }

    @Override
    public CameraSettings getCurrentCameraSettings() {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    protected boolean absPanTilt(int panValue, int tiltValue) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected boolean absPan(int value) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected boolean absTilt(int value) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected boolean absZoom(int value) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected boolean absFocus(int value) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected boolean relPan(int offset) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected boolean relTilt(int offset) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected boolean relZoom(int offset) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected boolean relFocus(int offset) {
        // TODO Auto-generated method stub
        return false;
    }
}
