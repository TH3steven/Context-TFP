package nl.tudelft.contextproject.script;

import static org.junit.Assert.assertEquals;

import nl.tudelft.contextproject.camera.Camera;
import nl.tudelft.contextproject.camera.CameraSettings;
import nl.tudelft.contextproject.presets.InstantPreset;
import nl.tudelft.contextproject.presets.Preset;

import org.junit.After;
import org.junit.Test;

/**
 * Class to test Shot Class. Test will be expanded with time.
 * @since 0.2
 */
public class ShotTest {
    
    @After
    public void cleanUp() {
        Camera.clearAllCameras();
    }
    
    /**
     * Test the constructor of the shot class.
     * Also tests if setters work properly.
     */
    @Test
    public  void testShot() {
        Camera cam0 = new Camera();
        Camera cam1 = new Camera();
        Preset pres = new InstantPreset(new CameraSettings(1, 1, 1, 2), 1);
        Preset pres2 = new InstantPreset(new CameraSettings(1, 2, 3, 4), 2);
        Shot shot1 = new Shot(1, cam0, pres);
        Shot shot2 = new Shot(0, null, null);
        Shot shot3 = new Shot(2, "2!", cam1, pres, "cover main podium");
        assertEquals(shot3.getDescription(), "cover main podium");
        assertEquals(shot3.getShotId(), "2!");
        assertEquals(shot3.getCamera(), cam1);
        assertEquals(shot3.getPreset(), pres);
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
        Preset pres = new InstantPreset(new CameraSettings(1, 1, 1, 2), 1);
        Shot shot1 = new Shot(1, cam0, pres);
        shot1.execute();
        assertEquals(cam0.getSettings().getFocus(), 2);
        assertEquals(cam0.getSettings().getPan(), 1);
        assertEquals(cam0.getSettings().getTilt(), 1);
        assertEquals(cam0.getSettings().getZoom(), 1);
    }
}
