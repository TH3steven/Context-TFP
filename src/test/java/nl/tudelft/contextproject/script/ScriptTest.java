package nl.tudelft.contextproject.script;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import nl.tudelft.contextproject.camera.Camera;
import nl.tudelft.contextproject.camera.CameraSettings;
import nl.tudelft.contextproject.presets.InstantPreset;
import nl.tudelft.contextproject.presets.Preset;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Class to test Script class. Test suite will be expanded
 * with time.
 *
 * @since 0.2
 */
public class ScriptTest {

    private Camera cam1;
    private Camera cam0;
    private Preset pres;
    private Preset pres2;
    private Preset pres3;
    private List<Shot> los;
    private List<Shot> los1;
    private Shot shot1;
    private Shot shot2;
    private Shot shot3;
    private Script script1;
    private Script script2;

    @After
    public void cleanUp() {
        Camera.clearAllCameras();
    }

    /**
     * Initialises the above private variables before each test.
     */
    @Before
    public void init() {
        cam0 = new Camera();
        cam1 = new Camera();
        pres = new InstantPreset(new CameraSettings(1, 1, 1, 2), 1);
        pres2 = new InstantPreset(new CameraSettings(1, 3, 2, 5), 2);
        pres3 = new InstantPreset(new CameraSettings(2, 4, 5, 3), 3);
        shot1 = new Shot(1, cam0, pres);
        shot2 = new Shot(2, cam1, pres2);
        shot3 = new Shot(3, cam0, pres3);
        los = new ArrayList<>();
        los1 = new ArrayList<>();
        los.add(shot1);
        los.add(shot2);
        los.add(shot3);
        script1 = new Script(los);
        script2 = new Script(los1);
    }
    
    /**
     * Test the script constructor to find the shots
     * available in the script.
     * This tests initTimeline as well to check if the timelines
     * are well initialized.
     */
    @Test
    public void testScript() {
        assertEquals(script1.getShots().get(0), shot1);
        assertFalse(script1.isEmpty());
        assertNotNull(script1);
        assertEquals(script1.getShots().get(0).getCamera(), cam0);
        assertEquals(script1.getShots().get(0).getNumber(), 1);
        assertEquals(script1.getShots().get(0).getPreset(), pres);
        assertFalse(script1.getTimeline(cam0.getNumber()) == script1.getTimeline(cam1.getNumber()));
        assertEquals(script1.getTimeline(cam0.getNumber()).getCamera(), cam0);
        assertNull(script1.getTimeline(4));
        assertEquals(script1.getTimeline(cam0.getNumber()).getShots().get(0), shot1);
        assertEquals(script1.getTimeline(cam0.getNumber()).getShots().get(1), shot3);
    }

    /**
     * Test hasNext Method.
     * This checks if there our script contains another shot,
     * to be taken after the current shot.
     */
    @Test
    public void testHasNext() {
        assertFalse(script2.hasNext());
        assertTrue(script1.hasNext());
    }
    
    /**
     * Test if the initPresetLoading method actually loads the presets.
     */
    @Test
    public void testInitPresetLoading() {
        assertEquals(cam0.getSettings(), new CameraSettings(1, 1, 1, 2));
    }

    /**
     * Test next method.
     * Retrieves and gives the next element of a list.
     * Throws an exception is list is empty.
     */
    @Test
    public  void testNext() throws NoSuchElementException {
        assertEquals(script1.next(), shot1);
        assertFalse(script2.hasNext());
        assertEquals(script1.next(), shot2);
        script1.next();
        assertFalse(script1.hasNext());
    }

    /**
     * Tests getCurrentShot method.
     * Retrieves and gets the current shot on a script.
     * Tests getNextShot method as well.
     * This gets the shot after the current shot on a script.
     * Throws an exception if this does not exist.
     */
    @Test
    public void testGetCurrentShot() {
        assertNull(script1.getCurrentShot());
        assertTrue(script1.hasNext());
        assertEquals(script1.getNextShot(), shot1);
        assertTrue(script1.hasNext());
        script1.next();
        assertEquals(script1.getNextShot(), shot2);
    }

    /**
     * Tests the isValid() method with a valid script.
     */
    @Test
    public void testIsValidTrue() {
        assertNull(script1.isValid());
        assertTrue(script1.showValid(1));
        assertTrue(script1.showValid(2));
    }
    
    /**
     * Tests the isValid() method with an invalid script.
     */
    @Test
    public void testIsValidFalse() {
        Shot shot1 = new Shot(1, cam1, pres);
        Shot shot2 = new Shot(2, cam0, pres2);
        Shot shot3 = new Shot(3, cam0, pres3);
        List<Shot> shots = new ArrayList<>();
        shots.add(shot1);
        shots.add(shot2);
        shots.add(shot3);
        Script script = new Script(shots);
        assertNotNull(script.isValid());        
    }
    
    /**
     * Tests the isValid() method with a short script.
     */
    @Test
    public void testIsValidShort() {
        Shot shot1 = new Shot(1, cam0, pres);
        List<Shot> shots = new ArrayList<>();
        shots.add(shot1);
        Script script = new Script(shots);
        assertNull(script.isValid());
    }

}