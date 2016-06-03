package nl.tudelft.contextproject.presets;

import static org.junit.Assert.assertEquals;

import nl.tudelft.contextproject.camera.Camera;
import nl.tudelft.contextproject.camera.CameraSettings;
import nl.tudelft.contextproject.camera.MockedCameraConnection;

import org.junit.After;
import org.junit.Test;

/**
 * Class to test preset InstantPreset.
 */
public class InstantPresetTest {
    
    @After
    public void cleanUp() {
        Camera.clearAllCameras();
    }

    /**
     * Tests the only important method in this preset: {@link InstantPreset#applyTo(Camera)}.
     */
    @Test
    public void testApply() {
        Camera cam = new Camera(new CameraSettings(1, 33, 7, 10));
        cam.setConnection(new MockedCameraConnection());
        Preset p = new InstantPreset(new CameraSettings(65, 65, 65, 65), 1);
        p.applyTo(cam);
        assertEquals(65, cam.getSettings().getPan());
        assertEquals(65, cam.getSettings().getTilt());
        assertEquals(65, cam.getSettings().getZoom());
        assertEquals(65, cam.getSettings().getFocus());
    }

}
