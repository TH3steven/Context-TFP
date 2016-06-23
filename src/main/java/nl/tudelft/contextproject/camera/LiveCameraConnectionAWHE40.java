package nl.tudelft.contextproject.camera;

public class LiveCameraConnectionAWHE40 extends LiveCameraConnection {

    public static final String CAMERA_MODEL = "AW-HE40";
    
    public LiveCameraConnectionAWHE40(String address) {
        super(address);
    }
    
    @Override
    public String getStreamLink() {
        return "rtsp://" + getAddress() + "/MediaInput/h264";
    }
}
