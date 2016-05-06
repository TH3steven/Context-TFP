package test.java.nl.tudelft.contextproject.script;

import static org.junit.Assert.assertEquals;

import main.java.nl.tudelft.contextproject.camera.Camera;
import main.java.nl.tudelft.contextproject.camera.CameraSettings;
import main.java.nl.tudelft.contextproject.presets.InstantPreset;
import main.java.nl.tudelft.contextproject.presets.Preset;
import main.java.nl.tudelft.contextproject.script.Shot;

import org.junit.Test;

/**
 * Class to test Shot Class. Test will be expanded with time.
 *
 * @author  Etta Tabe Takang Kajikaw
 * @since 0.2
 */
public class ShotTest {
    
    /**
     * Test the constructor of the shot class.
     * Also tests if setters work properly.
     */
    @Test
    public  void testShot() {
        Camera cam0 = new Camera();
        Camera cam1 = new Camera();
        Preset pres = new InstantPreset(new CameraSettings(1, 1, 1, 2));
        Preset pres2 = new InstantPreset(new CameraSettings(1, 2, 3, 4));
        Shot shot1 = new Shot(1, cam0, pres);
        Shot shot2 = new Shot(0, null, null);
        assertEquals(shot1.getCamera(), cam0);
        assertEquals(shot1.getNumber(), 1);
        assertEquals(shot1.getPreset(), pres);
        shot2.setCamera(cam1);
        shot2.setNumber(2);
        shot2.setPreset(pres2);
        assertEquals(shot2.getCamera(), cam1);
        assertEquals(shot2.getNumber(), 2);
        assertEquals(shot2.getPreset(), pres2);
    }

    /**
     * Test the execute method.
     * This tests if the preset settings are applied to a camera
     * after the call to the execute method.
     */
    @Test
    public void testExecute() {
        Camera cam0 = new Camera();
        Preset pres = new InstantPreset(new CameraSettings(1, 1, 1, 2));
        Shot shot1 = new Shot(1, cam0, pres);
        shot1.execute();
        assertEquals(cam0.getSettings().getFocus(), 1365);
        assertEquals(cam0.getSettings().getPan(), 1);
        assertEquals(cam0.getSettings().getTilt(), 1);
        assertEquals(cam0.getSettings().getZoom(), 1);
    }
}
