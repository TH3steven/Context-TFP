package nl.tudelft.contextproject.camera;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

import nl.tudelft.contextproject.gui.LiveStreamHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Observable;
import javax.imageio.ImageIO;

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

    public static final int PAN_LIMIT_LOW = 11528;
    public static final int PAN_LIMIT_HIGH = 54005;

    public static final int TILT_LIMIT_LOW = 7283;
    public static final int TILT_LIMIT_HIGH = 36408;

    public static final int ZOOM_LIMIT_LOW = 1365;
    public static final int ZOOM_LIMIT_HIGH = 4095;

    public static final int FOCUS_LIMIT_LOW = 1365;
    public static final int FOCUS_LIMIT_HIGH = 4095;

    private static final int READ_TIMEOUT = 1000;

    private final String errorString = "Wrong response from camera: ";

    private boolean connected;
    private boolean autoFocus;
    private CameraSettings lastKnown;
    private String address;

    /**
     * Creates a LiveCameraConnection object. Assumes that the
     * address given is the correctly formulated IP address of the
     * camera to connect to.
     * 
     * @param address IP address of the camera to connect to.
     */
    public LiveCameraConnection(String address) {
        this.address = address;
        this.connected = false;
    }

    /**
     * Returns the last known camera settings.
     * @return The last known camera settings.
     */
    protected CameraSettings getLastKnownSettings() {
        return lastKnown;
    }
    
    protected void setLastKnownSettings(CameraSettings settings) {
        this.lastKnown = settings;
    }
    
    protected void setConnected(boolean connected) {
        this.connected = connected;
    }
    
    /**
     * Returns the address of this connection.
     * @return the address of this connection.
     */
    public String getAddress() {
        return address;
    }

    @Override
    public boolean setUpConnection() {
        try {
            String cameraModel = sendRequest(buildCamControlURL("QID"));
            System.out.println(cameraModel);
            if (cameraModel.equals("OID:" + CAMERA_MODEL)) {
                connected = true;
                lastKnown = new CameraSettings();
                lastKnown = getCurrentCameraSettings();
                hasAutoFocus();
                return true;
            }

            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    @Override
    public String getStreamLink() {
        return "rtsp://" + address + "/MediaInput/h264";
    }

    /**
     * Builds the URL for the command specified, which must be a command
     * from section 3.1 Pan-tilt Head Control.
     * 
     * @param command Command to be sent.
     * @return The formed URL, according to the {@link #address} and the command.
     * @throws MalformedURLException if the parameter command is null.
     */
    protected URL buildPanTiltHeadControlURL(String command) throws MalformedURLException {
        if (command != null) {
            URL url = new URL("http://" + address + "/cgi-bin/aw_ptz?cmd=" + command + "&res=1");
            return url;
        }

        throw new MalformedURLException("Given command is null");
    }

    /**
     * Builds the URL for the command specified, which must be a command
     * from section 3.2 Camera Control.
     * 
     * @param command Command to be sent.
     * @return The formed URL, according to the {@link #address} and the command.
     * @throws MalformedURLException if the parameter command is null.
     */
    protected URL buildCamControlURL(String command) throws MalformedURLException {
        if (command != null) {
            URL url = new URL("http://" + address + "/cgi-bin/aw_cam?cmd=" + command + "&res=1");
            return url;
        }

        throw new MalformedURLException("Given command is null");
    }

    /**
     * Sends the HTTP request specified in the URL as a GET request.
     * It waits for a response from the server until a response is
     * received or until the connection times out, which happens after
     * the amount of milliseconds specified in {@link #READ_TIMEOUT}.
     * An empty string is returned if there was no response.
     * 
     * @param url The URL containing the full HTTP request 
     * @return The response of the server. 
     * @throws IOException when something goes wrong in opening the
     *      the connection or reading the response from the server.
     */
    protected String sendRequest(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        System.out.println(url);
        try {
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(READ_TIMEOUT);
            connection.connect();
        } catch (SocketTimeoutException e) {
            connected = false;
            return "";
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
        String response = reader.readLine();
        reader.close();

        return response != null ? response : "";
    }

    /**
     * Returns true if the camera is on auto focus.
     * @return True if the camera is on auto focus.
     */
    public boolean hasAutoFocus() {
        if (connected) {
            try {
                String autoFocusRes = sendRequest(buildPanTiltHeadControlURL("%23D1"));

                if (autoFocusRes.startsWith("d1")) {
                    autoFocus = Integer.parseInt(autoFocusRes.substring(2)) == 1;
                } else {
                    throw new IOException(errorString + autoFocusRes);
                }
            } catch (IOException e) {
                e.printStackTrace();
                return autoFocus;
            }
        }

        return autoFocus;
    }

    /**
     * Sets the auto focus setting on the camera to on (true) or off (false).
     * 
     * @param autoFocus true for ON, false for OFF
     * @return True iff the camera was set to the specified setting.
     */
    public boolean setAutoFocus(boolean autoFocus) {
        if (this.autoFocus == autoFocus) {
            return true;
        }

        try {
            int set = autoFocus ? 1 : 0;
            String autoFocusRes = sendRequest(buildPanTiltHeadControlURL("%23D1" + set));

            if (autoFocusRes.equals("d1" + set)) {
                this.autoFocus = autoFocus;
                return true;
            }

            throw new IOException(errorString + autoFocusRes);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if (!(o instanceof Camera)) {
            return;
        }

        if (arg instanceof CameraSettings) {
            mutateSettings((CameraSettings) arg);
        }
    }

    /**
     * Finds the least amount of commands to send to the 
     * camera in order to apply the specified camera settings.
     * 
     * @param toSet Camera settings to apply to the camera.
     * @return True iff the camera was set to the specified settings.
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

        if (!hasAutoFocus() && curSettings.getFocus() != toSet.getFocus()) {
            result = result && absFocus(toSet.getFocus());
        }

        return result;
    }

    @Override
    public CameraSettings getCurrentCameraSettings() {
        int[] panTilt = getCurrentPanTilt();
        int zoom = getCurrentZoom();
        int focus = getCurrentFocus();
        lastKnown = new CameraSettings(panTilt[0], panTilt[1], zoom, focus);

        return lastKnown;
    }

    @Override
    public int[] getCurrentPanTilt() {
        try {
            String panTiltRes = sendRequest(buildPanTiltHeadControlURL("%23APC"));

            if (panTiltRes.startsWith("aPC")) {
                int pan = Integer.parseInt(panTiltRes.substring(3, 7), 16);
                int tilt = Integer.parseInt(panTiltRes.substring(7, 11), 16);
                lastKnown.setPan(pan);
                lastKnown.setTilt(tilt);
                return new int[] {pan, tilt};
            }
            throw new IOException(errorString + panTiltRes);
        } catch (IOException e) {
            e.printStackTrace();
            return new int[]{lastKnown.getPan(), lastKnown.getTilt()};
        }
    }

    @Override
    public int getCurrentZoom() {
        try {
            String zoomRes = sendRequest(buildPanTiltHeadControlURL("%23GZ"));

            if (zoomRes.startsWith("gz")) {
                int zoom = Integer.parseInt(zoomRes.substring(2, 5), 16);
                lastKnown.setZoom(zoom);
                return zoom;
            }

            throw new IOException(errorString + zoomRes);
        } catch (IOException e) {
            e.printStackTrace();
            return lastKnown.getZoom();
        }
    }

    @Override
    public int getCurrentFocus() {
        if (autoFocus) {
            return -1;
        }

        try {
            String focusRes = sendRequest(buildPanTiltHeadControlURL("%23GF"));

            if (focusRes.startsWith("gf")) {
                int focus = Integer.parseInt(focusRes.substring(2), 16);
                return focus;
            }

            throw new IOException(errorString + focusRes);
        } catch (IOException e) {
            e.printStackTrace();
            return lastKnown.getFocus();
        }
    }

    @Override
    protected boolean absPanTilt(int panValue, int tiltValue) {
        panValue = roundToBounds(panValue, PAN_LIMIT_LOW, PAN_LIMIT_HIGH);
        tiltValue = roundToBounds(tiltValue, TILT_LIMIT_LOW, TILT_LIMIT_HIGH);

        try {
            String res = sendRequest(buildPanTiltHeadControlURL(
                    "%23APS" 
                            + Integer.toHexString(0x10000 | panValue).substring(1).toUpperCase() 
                            + Integer.toHexString(0x10000 | tiltValue).substring(1).toUpperCase()
                            + "1D" + "2"
                    ));

            if (res.startsWith("aPS")) {
                lastKnown.setPan(panValue);
                lastKnown.setTilt(tiltValue);
                return true;
            }

            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected boolean absPan(int value) {
        return absPanTilt(value, lastKnown.getTilt());
    }

    @Override
    protected boolean absTilt(int value) {
        return absPanTilt(lastKnown.getPan(), value);
    }

    @Override
    protected boolean absZoom(int value) {
        value = roundToBounds(value, ZOOM_LIMIT_LOW, ZOOM_LIMIT_HIGH);
        try {
            String res = sendRequest(buildPanTiltHeadControlURL("%23AXZ" 
                    + Integer.toHexString(0x1000 | value).substring(1).toUpperCase()
                    ));

            if (res.startsWith("axz")) {
                lastKnown.setZoom(value);
                return true;
            }

            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected boolean absFocus(int value) {
        value = roundToBounds(value, FOCUS_LIMIT_LOW, FOCUS_LIMIT_HIGH);

        try {
            if (autoFocus) {
                throw new IOException("Autofocus is on");
            }

            String res = sendRequest(buildPanTiltHeadControlURL("%23AXF" 
                    + Integer.toHexString(0x1000 | value).substring(1).toUpperCase()
                    ));

            if (res.startsWith("axf")) {
                lastKnown.setFocus(value);
                return true;
            } else if (res.startsWith("ER3")) {
                autoFocus = true;
                throw new IOException("Autofocus is on");
            }

            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected boolean relPanTilt(int panOffset, int tiltOffset) {
        CameraSettings curSet = getCurrentCameraSettings();

        if (curSet.getPan() + panOffset < PAN_LIMIT_LOW) {
            panOffset = curSet.getPan() - PAN_LIMIT_LOW;
        } else if (curSet.getPan() + panOffset > PAN_LIMIT_HIGH) {
            panOffset = PAN_LIMIT_HIGH - curSet.getPan();
        }

        if (curSet.getTilt() + tiltOffset < TILT_LIMIT_LOW) {
            tiltOffset = curSet.getTilt() - TILT_LIMIT_LOW;
        } else if (curSet.getTilt() + tiltOffset > TILT_LIMIT_HIGH) {
            tiltOffset = TILT_LIMIT_HIGH - curSet.getTilt();
        }

        try {
            String res = sendRequest(buildPanTiltHeadControlURL("%23RPC" 
                    + Integer.toHexString(0x10000 | 32768 + panOffset).substring(1).toUpperCase() 
                    + Integer.toHexString(0x10000 | 32768 + tiltOffset).substring(1).toUpperCase()
                    ));

            if (res.startsWith("rPC")) {
                lastKnown.pan(panOffset);
                lastKnown.tilt(tiltOffset);
                return true;
            }

            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected boolean relPan(int offset) {
        return relPanTilt(offset, 0);
    }

    @Override
    protected boolean relTilt(int offset) {
        return relPanTilt(0, offset);
    }

    @Override
    protected boolean relZoom(int offset) {
        return absZoom(getCurrentZoom() + offset);
    }

    @Override
    protected boolean relFocus(int offset) {
        return absFocus(getCurrentFocus() + offset);
    }
    
    @Override
    protected boolean panTiltStart(int panSpeed, int tiltSpeed) {
        if (panSpeed == 50 && tiltSpeed == 50) {
            return panTiltStop();
        }
        
        panSpeed = roundToBounds(panSpeed, 1, 99);
        tiltSpeed = roundToBounds(tiltSpeed, 1, 99);
        
        try {
            String res = sendRequest(buildPanTiltHeadControlURL("%23PTS" 
                            + String.format("%02d", panSpeed) 
                            + String.format("%02d", tiltSpeed))
                         );
            if (res.startsWith("pTS")) {
                return true;
            }
            return false;
        } catch (IOException e) {
            return false;
        }
    }
    
    @Override
    protected boolean panTiltStop() {
        try {
            String res = sendRequest(buildPanTiltHeadControlURL("%23PTS5050"));
            if (res.equals("pTS5050")) {
                return true;
            }
            return false;
        } catch (IOException e) {
            return false;
        }
    }
    
    /**
     * Enforces that a number is between the given bounds. If not, then it will be
     * rounded to the closest bound.
     * 
     * @param number The number to check
     * @param boundLow The lower bound
     * @param boundHigh The upper bound
     * @return The number, within the specified bounds.
     */
    private int roundToBounds(int number, int boundLow, int boundHigh) {
        return number < boundLow ? boundLow : number > boundHigh ? boundHigh : number;
    }
}
