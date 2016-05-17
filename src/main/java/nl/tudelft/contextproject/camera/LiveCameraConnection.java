package main.java.nl.tudelft.contextproject.camera;

import java.util.Observable;

/**
 * Class to represent a live connection with a camera. It is
 * responsible for the communication between our data model and
 * the actual camera, in our case a Panasonic AW-HE130.
 * 
 * @since 0.4
 */
public class LiveCameraConnection extends CameraConnection {
    
    public static final String CAMERA_MODEL = "AW-HE130";

    @Override
    public void update(Observable o, Object arg) {
        // TODO Auto-generated method stub

    }

    @Override
    public CameraSettings getCurrentCameraSettings() {
        // TODO Auto-generated method stub
        return null;
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
