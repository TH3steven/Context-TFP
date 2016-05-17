package main.java.nl.tudelft.contextproject.camera;

import java.util.Observable;

/**
 * Class to represent a live connection with a camera. It is
 * responsible for the communication between our data model and
 * the actual camera, in this case a Panasonic AW-HE130.
 * 
 * @since 0.4
 */
public class LiveCameraConnection extends CameraConnection {
    
    public static final String CAMERA_MODEL = "AW-HE130";
    
    private boolean autoFocus;

    @Override
    public void update(Observable o, Object arg) {
        if (!(o instanceof Camera)) {
            return;
        }
        Camera cam = (Camera) o;
        if (arg instanceof CameraSettings) {
            mutateSettings(cam, (CameraSettings) arg);
        }
    }
    
    private boolean mutateSettings(Camera cam, CameraSettings toSet) {
        CameraSettings curSettings = getCurrentCameraSettings();
        boolean result = true;
        if (curSettings.getPan() != toSet.getPan() 
                || curSettings.getPan() != toSet.getPan()) {
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
