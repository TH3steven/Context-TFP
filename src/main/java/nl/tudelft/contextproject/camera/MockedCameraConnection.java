package main.java.nl.tudelft.contextproject.camera;

import java.util.Observable;

/**
 * Class to represent a mocked camera. It mimics or mocks the behavior of a specific camera
 * without having a real connection to the cameras.
 */
public class MockedCameraConnection extends CameraConnection {
    private CameraSettings camSet = new CameraSettings(30, 30, 30, 1365);

    @Override
    public CameraSettings getCurrentCameraSettings() {
        return camSet;
    }

    @Override
    protected boolean absPan(int value) {
        camSet.setPan(value);
        return true;
    }

    @Override
    protected boolean absTilt(int value) {
        camSet.setTilt(value);
        return true;
    }

    @Override
    protected boolean absZoom(int value) {
        camSet.setZoom(value);
        return true;
    }

    @Override
    protected boolean absFocus(int value) {
        camSet.setFocus(value);
        return true;
    }

    @Override
    protected boolean relPan(int offset) {
        camSet.pan(offset);
        return true;
    }

    @Override
    protected boolean relTilt(int offset) {
        camSet.tilt(offset);
        return true;
    }

    @Override
    protected boolean relZoom(int offset) {
        camSet.zoom(offset);
        return true;
    }

    @Override
    protected boolean relFocus(int offset) {
        camSet.focus(offset);
        return true;
    }

    @Override
    public void update(Observable o, Object arg) {

    }
}
