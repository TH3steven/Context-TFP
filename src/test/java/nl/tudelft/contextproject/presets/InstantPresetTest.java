package nl.tudelft.contextproject.presets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import nl.tudelft.contextproject.camera.Camera;
import nl.tudelft.contextproject.camera.CameraSettings;
import nl.tudelft.contextproject.camera.MockedCameraConnection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Class to test preset InstantPreset.
 */
public class InstantPresetTest {
    private Camera cam;
    private CameraSettings camSet;
    private CameraSettings camSet2;
    private Preset preset1;
    private Preset preset2;

    @After
    public void cleanUp() {
        Camera.clearAllCameras();
    }

    @Before
    public void init() {
        cam = new Camera();
        camSet = new CameraSettings(1, 33, 7, 10);
        camSet2 = new CameraSettings(65, 65, 65, 65);
        cam.setSettings(camSet);
        cam.setConnection(new MockedCameraConnection());
        preset1 = new InstantPreset(camSet2, 1);
        preset2 = new InstantPreset(camSet, 2, "rotate camera to the left");
    }
    /**
     * Tests the only important method in this preset: {@link InstantPreset#applyTo(Camera)}.
     */
    @Test
    public void testApply() {
        preset1.applyTo(cam);
        assertEquals(65, cam.getSettings().getPan());
        assertEquals(65, cam.getSettings().getTilt());
        assertEquals(65, cam.getSettings().getZoom());
        assertEquals(65, cam.getSettings().getFocus());
    }

    /**
     * Tests if the right presets are applied to a camera.
     * And tests the second constructor of preset.
     */
    @Test
    public void testApply2() {
        preset2.applyTo(cam);
        assertEquals("rotate camera to the left", preset2.getDescription());
        assertEquals(2, preset2.getId());
        assertNotEquals(preset1,preset2);
    }
}
