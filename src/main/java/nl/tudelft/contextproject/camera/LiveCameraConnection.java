package nl.tudelft.contextproject.camera;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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
    
    private static final int READ_TIMEOUT = 5000;
    
    private String address;
    private boolean connected;
    private boolean autoFocus;
    private CameraSettings lastKnown;
    
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
        try {
            URL url = buildCamControlURL("QID");
            if (("OID:" + CAMERA_MODEL).equals(sendRequest(url))) {
                String autoFocusResponse = sendRequest(buildPanTiltHeadControlURL("%23D1"));
                int hasAutoFocus = Integer.parseInt(autoFocusResponse.substring(2));
                autoFocus = hasAutoFocus == 1;
                connected = true;
                lastKnown = new CameraSettings();
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
    
    /**
     * Builds the URL for the command specified, which must be a command
     * from section 3.1 Pan-tilt Head Control.
     * @param command Command to be sent.
     * @return the formed URL, according to the {@link #address} and the command.
     * @throws MalformedURLException if the parameter command is null.
     */
    private URL buildPanTiltHeadControlURL(String command) throws MalformedURLException {
        if (command != null) {
            URL url = new URL("http://" + address + "/cgi-bin/aw_ptz?cmd=" + command + "&res=1");
            return url;
        } else {
            throw new MalformedURLException("Given command is null");
        }
    }
    
    /**
     * Builds the URL for the command specified, which must be a command
     * from section 3.2 Camera Control.
     * @param command Command to be sent.
     * @return the formed URL, according to the {@link #address} and the command.
     * @throws MalformedURLException if the parameter command is null.
     */
    private URL buildCamControlURL(String command) throws MalformedURLException {
        if (command != null) {
            URL url = new URL("http://" + address + "/cgi-bin/aw_cam?cmd=" + command + "&res=1");
            return url;
        } else {
            throw new MalformedURLException("Given command is null");
        }
    }
    
    /**
     * Sends the HTTP request specified in the URL as a POST request.
     * It waits for a response from the server until a response is
     * received or until the connection times out, which happens after
     * the amount of milliseconds specified in {@link #READ_TIMEOUT}.
     * @param url the URL containing the full HTTP request 
     * @return the response of the server. 
     * @throws IOException when something goes wrong in opening the
     *      the connection or reading the response from the server.
     * @throws java.net.SocketTimeoutException when a timeout has
     *      occurred while connecting to the server.
     */
    private String sendRequest(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setReadTimeout(READ_TIMEOUT);
        connection.connect();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String response = reader.readLine();
        reader.close();
        return response != null ? response : "";
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
        //Camera cam = (Camera) o;
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
        try {
            URL panTiltUrl = buildPanTiltHeadControlURL("%23APC");
            URL zoomUrl = buildPanTiltHeadControlURL("%23GZ");
            String panTiltRes = sendRequest(panTiltUrl);
            String zoomRes = sendRequest(zoomUrl);
            if (panTiltRes != null && panTiltRes.startsWith("aPC") && zoomRes.startsWith("gz")) {
                int pan = Integer.parseInt(panTiltRes.substring(3, 7), 16);
                int tilt = Integer.parseInt(panTiltRes.substring(7, 11), 16);
                int zoom = (int) Math.round((Integer.parseInt(zoomRes.substring(2, 5), 16) - 1365) / 27.3);
                int focus = lastKnown.getFocus(); //TODO Fix this.
                lastKnown = new CameraSettings(pan, tilt, zoom, focus);
                return lastKnown;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return lastKnown;
        }
        return lastKnown;
    }
    
    @Override
    protected boolean absPanTilt(int panValue, int tiltValue) {
        try {
            String res = sendRequest(buildPanTiltHeadControlURL(
                        "%23APS" 
                        + Integer.toHexString( 0x10000 | panValue).substring(1).toUpperCase() 
                        + Integer.toHexString( 0x10000 | tiltValue).substring(1).toUpperCase()
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
        try {
            String res = sendRequest(buildPanTiltHeadControlURL(
                        "%23AXZ" + Integer.toHexString(value)
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
        try {
            if (autoFocus) {
                throw new IOException("Autofocus is on");
            }
            String res = sendRequest(buildPanTiltHeadControlURL(
                        "%23AXF" + Integer.toHexString(value)
                    ));
            if (res.startsWith("axf")) {
                lastKnown.setZoom(value);
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
        try {
            String res = sendRequest(buildPanTiltHeadControlURL(
                        "%23RPC" + Integer.toHexString(panOffset) + Integer.toHexString(tiltOffset)
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
        absZoom(lastKnown.getZoom() + offset);
        return false;
    }

    @Override
    protected boolean relFocus(int offset) {
        absFocus(lastKnown.getZoom() + offset);
        return false;
    }
}
