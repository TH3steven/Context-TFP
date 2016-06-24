package nl.tudelft.contextproject.script;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import nl.tudelft.contextproject.camera.Camera;
import nl.tudelft.contextproject.camera.CameraSettings;
import nl.tudelft.contextproject.camera.MockedCameraConnection;
import nl.tudelft.contextproject.presets.InstantPreset;
import nl.tudelft.contextproject.presets.Preset;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Class to test Script class.
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
    private Shot dummyShot;
    private Script script1;
    private Script script2;
    private Timeline timeline1;

    /**
     * Initialises the above private variables before each test.
     */
    @Before
    public void init() {
        cam0 = new Camera();
        cam1 = new Camera();
        cam0.setConnection(new MockedCameraConnection());
        cam1.setConnection(new MockedCameraConnection());
        
        pres = new InstantPreset(new CameraSettings(1, 1, 1, 2), 1);
        pres2 = new InstantPreset(new CameraSettings(1, 3, 2, 5), 2);
        pres3 = new InstantPreset(new CameraSettings(2, 4, 5, 3), 3);
        
        shot1 = new Shot(1, cam0, pres);
        shot2 = new Shot(2, cam1, pres2);
        shot3 = new Shot(3, cam0, pres3);
        dummyShot = new Shot(-1, "-1", Camera.DUMMY, 
                new InstantPreset(new CameraSettings(), -1), "No shot", "No action");
        
        los = new ArrayList<>();
        los1 = new ArrayList<>();
        los.add(shot1);
        los.add(shot2);
        los.add(shot3);
        
        script1 = new Script(los);
        script2 = new Script(los1);
        timeline1 = new Timeline(cam0, los1);
    }
    
    @After
    public void cleanUp() {
        Camera.clearAllCameras();
    }
    
    /**
     * Test the script constructor to find the shots
     * available in the script.
     * This tests initTimeline as well to check if the timelines
     * are well initialized.
     * Also tests the getShots method.
     */
    @Test
    public void testScript() {
        assertNotEquals(script1.getTimeline(cam0.getNumber()), script1.getTimeline(cam1.getNumber()));
        assertEquals(script1.getTimeline(cam0.getNumber()).getCamera(), cam0);
        assertNull(script1.getTimeline(4));
        assertEquals(script1.getTimeline(cam0.getNumber()).getShots().get(0), shot1);
        assertEquals(script1.getTimeline(cam0.getNumber()).getShots().get(1), shot3);
    }

    /**
     * Continuation of the script constructor test.
     */
    @Test
    public void testScript1() {
        assertEquals(script1.getShots().get(0).getCamera(), cam0);
        assertEquals(script1.getShots().get(0).getNumber(), 1);
        assertEquals(script1.getShots().get(0).getPreset(), pres);
        assertNotEquals(script1, script2);
        assertNotEquals(script1, los1);
    }

    /**
     * Test for the script constructor.
     * Has been split to avoid PMD errors.
     */
    @Test
    public void testScript2() {
        assertEquals(script1.getShots().get(0), shot1);
        assertFalse(script1.isEmpty());
        assertNotNull(script1);
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
        script1.initPresetLoading();
        assertEquals(cam0.getSettings(), new CameraSettings(1, 1, 1, 2));
    }

    /**
     * Test next method.
     * Retrieves and gives the next element of a list.
     * Throws an exception is list is empty.
     */
    @Test
    public void testNext() throws NoSuchElementException {
        assertEquals(script1.next(true), shot1);
        assertEquals(script1.next(true), shot2);
        script1.next();
        assertFalse(script1.hasNext());
        assertNull(script1.getNextShot());
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
        assertEquals(script1.getCurrentShot(), dummyShot);
        assertTrue(script1.hasNext());
        assertEquals(script1.getNextShot(), shot1);
        assertTrue(script1.hasNext());
        script1.next();
        assertEquals(script1.getCurrentShot(), shot1);
    }

    /**
     * Tests the isValid() method with a valid script.
     * Also tests the showValid() method with a valid script.
     */
    @Test
    public void testIsValidTrue() {
        assertNull(script1.isValid());
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
        assertEquals(script.isValid(), shot3);
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
        shots.clear();
        assertNull(script1.isValid());
    }

    /**
     * Tests the getShot method.
     * Also checks isEmpty method.
     */
    @Test
    public  void testGetShots() {
        List<Shot> shots = new ArrayList<>();
        shots.add(shot1);
        shots.add(shot2);
        shots.add(shot3);
        assertEquals(script1.getShots(), shots);
        assertFalse(script1.getShots().isEmpty());
        assertTrue(script2.isEmpty());
    }

    /**
     * Tests the addShot method if the shots are properly added to
     * the script.
     */
    @Test
    public void testAddShot() {
        los1.add(shot1);
        script2.addShot(shot1);
        assertEquals(script2.getShots(), los1);
        assertTrue(timeline1.getShots().contains(shot1));
    }

    /**
     * This tests the getCurrent method.
     */
    @Test
    public void testGetCurrent() {
        assertEquals(-1, script1.getCurrent());
        script1.next();
        script1.next();
        assertEquals(1, script1.getCurrent());
    }
}
