package nl.tudelft.contextproject.camera;

import java.io.IOException;

public class LiveCameraConnectionAWHE40 extends LiveCameraConnection {

    public static final String CAMERA_MODEL = "AW-HE40";
    
    public LiveCameraConnectionAWHE40(String address) {
        super(address);
    }
    
    @Override
    public boolean setUpConnection() {
        try {
            String cameraModel = sendRequest(buildCamControlURL("QID"));
            
            System.out.println(cameraModel);

            if (cameraModel.equals("OID:" + CAMERA_MODEL)) {
                setConnected(true);
                setLastKnownSettings(new CameraSettings());
                setLastKnownSettings(getCurrentCameraSettings());
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
    public String getStreamLink() {
        return "rtsp://" + getAddress() + "/MediaInput/h264";
    }
}
