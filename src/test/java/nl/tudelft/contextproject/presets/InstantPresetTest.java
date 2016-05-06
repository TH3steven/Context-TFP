package test.java.nl.tudelft.contextproject.presets;

import static org.junit.Assert.assertEquals;

import main.java.nl.tudelft.contextproject.camera.Camera;
import main.java.nl.tudelft.contextproject.camera.CameraSettings;
import main.java.nl.tudelft.contextproject.presets.InstantPreset;
import main.java.nl.tudelft.contextproject.presets.Preset;

import org.junit.Test;

/**
 * Class to test preset InstantPreset.
 */
public class InstantPresetTest {

    /**
     * Tests the only important method in this preset: {@link InstantPreset#applyTo(Camera)}.
     */
    @Test
    public void testApply() {
        Camera cam = new Camera(new CameraSettings(1, 33, 7, 10));
        Preset p = new InstantPreset(new CameraSettings(65, 65, 65, 65));
        p.applyTo(cam);
        assertEquals(65, cam.getSettings().getPan());
        assertEquals(65, cam.getSettings().getTilt());
        assertEquals(65, cam.getSettings().getZoom());
        assertEquals(1365, cam.getSettings().getFocus());
    }

}
