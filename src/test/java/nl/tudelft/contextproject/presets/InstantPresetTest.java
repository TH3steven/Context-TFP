package test.java.nl.tudelft.contextproject.presets;

import static org.junit.Assert.assertEquals;
import main.java.nl.tudelft.contextproject.camera.Camera;
import main.java.nl.tudelft.contextproject.camera.CameraSettings;
import main.java.nl.tudelft.contextproject.presets.InstantPreset;
import main.java.nl.tudelft.contextproject.presets.Preset;

import org.junit.Test;

public class InstantPresetTest {

    @Test
    public void testApply() {
        Camera cam = new Camera(new CameraSettings(1, 33, 7, 10));
        Preset p = new InstantPreset(cam, new CameraSettings(65, 65, 65, 65));
        p.apply();
        assertEquals(65, cam.getSettings().getPan());
        assertEquals(65, cam.getSettings().getTilt());
        assertEquals(65, cam.getSettings().getZoom());
        assertEquals(1365, cam.getSettings().getFocus());
    }

}
