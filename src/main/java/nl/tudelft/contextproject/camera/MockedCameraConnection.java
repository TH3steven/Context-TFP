package nl.tudelft.contextproject.camera;

import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Class to represent a mocked camera. It mimics or mocks the behavior of a specific camera
 * without having a real connection to the cameras.
 * 
 * @since 0.4
 */
public class MockedCameraConnection extends CameraConnection {

    private CameraSettings camSet = new CameraSettings(30, 30, 30, 1365);
    private String streamLink = "src\\main\\resources\\error-q.png";
    
    private Timer timer;

    @Override
    public boolean setUpConnection() {
        return true;
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public String getStreamLink() {
        return streamLink;
    }

    /**
     * Sets the link to the 'live stream' of this mocked camera connection
     * @param link Link to the 'live stream' of this mocked camera connection.
     */
    public void setStreamLink(String link) {
        streamLink = link;
    }

    @Override
    public CameraSettings getCurrentCameraSettings() {
        return camSet;
    }

    @Override
    public int[] getCurrentPanTilt() {
        return new int[]{camSet.getPan(), camSet.getTilt()};
    }

    @Override
    public int getCurrentZoom() {
        return camSet.getZoom();
    }

    @Override
    public int getCurrentFocus() {
        return camSet.getFocus();
    }

    @Override
    protected boolean absPanTilt(int panValue, int tiltValue) {
        camSet.setPan(panValue);
        camSet.setTilt(tiltValue);

        return true;
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
    protected boolean relPanTilt(int panOffset, int tiltOffset) {
        camSet.pan(panOffset);
        camSet.tilt(tiltOffset);

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
        if (!(o instanceof Camera)) {
            return;
        }

        if (arg instanceof CameraSettings) {
            camSet = (CameraSettings) arg;
        }
    }

    @Override
    protected boolean panTiltStart(int panSpeed, int tiltSpeed) {
        if (panSpeed == 50 && tiltSpeed == 50) {
            return panTiltStop();
        }
        
        timer = new Timer("CameraSettings Pan Tilt");
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                int panOff = (panSpeed - 50) * 2;
                int tiltOff = (tiltSpeed - 50) * 2;
                relPanTilt(panOff, tiltOff);
            }
        }, 0, 100);
        
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                timer.cancel();
            }
        }, 60000); //so it will automatically stop after a minute.
        
        return true;
    }

    @Override
    protected boolean panTiltStop() {
        if (timer != null) {
            timer.cancel();
        }
        return true;
    }
}
