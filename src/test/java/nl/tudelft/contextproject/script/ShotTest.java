package nl.tudelft.contextproject.script;

import static org.junit.Assert.assertEquals;

import nl.tudelft.contextproject.camera.Camera;
import nl.tudelft.contextproject.camera.CameraSettings;
import nl.tudelft.contextproject.presets.InstantPreset;
import nl.tudelft.contextproject.presets.Preset;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Class to test Shot Class. Test will be expanded with time.
 * @since 0.2
 */
public class ShotTest {

    private Camera cam0;
    private Camera cam1;
    private Preset pres;
    private Preset pres2;
    private Shot shot1;
    private Shot shot2;
    private Shot shot3;

    /**
     * Initializes the above private variables before each test.
     */
    @Before
    public void init() {
        cam0 = new Camera();
        cam1 = new Camera();
        pres = new InstantPreset(new CameraSettings(1, 1, 1, 2), 1);
        pres2 = new InstantPreset(new CameraSettings(1, 2, 3, 4), 2);
        shot1 = new Shot(1, cam0, pres);
        shot2 = new Shot(0, null, null);
        shot3 = new Shot(2, "2!", cam1, pres, "cover main podium");
    }
    
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
        shot1.setDuration(5.0);
        shot2.setDuration(3.5);
        shot3.setDuration(1.5);
        assertEquals(shot3.getDescription(), "cover main podium");
        assertEquals(shot3.getShotId(), "2!");
        assertEquals(shot3.getCamera(), cam1);
        assertEquals(shot3.getPreset(), pres);
        assertEquals(shot1.getCamera(), cam0);
        assertEquals(shot1.getNumber(), 1);
        assertEquals(shot1.getPreset(), pres);
        assertEquals(shot1.getDuration(), 4.5, 1);
        assertEquals(shot2.getDuration(), 3.0, 1);
        assertEquals(shot3.getDuration(), 1.0, 1);
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
        shot1.execute();
        assertEquals(cam0.getSettings().getFocus(), 2);
        assertEquals(cam0.getSettings().getPan(), 1);
        assertEquals(cam0.getSettings().getTilt(), 1);
        assertEquals(cam0.getSettings().getZoom(), 1);
    }
}
