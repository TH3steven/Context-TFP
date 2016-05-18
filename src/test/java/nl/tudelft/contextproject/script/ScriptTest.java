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

    @After
    public void cleanUp() {
        Camera.clearAllCameras();
    }
    
    /**
     * Test the script constructor to find the shots
     * available in the script.
     * This tests initTimeline as well to check if the timelines
     * are well initialized.
     */
    @Test
    public void testScript() {
        Camera cam0 = new Camera();
        Camera cam1 = new Camera();
        Preset pres = new InstantPreset(new CameraSettings(1, 1, 1, 2), 1);
        Preset pres2 = new InstantPreset(new CameraSettings(1, 3, 2, 5), 2);
        Preset pres3 = new InstantPreset(new CameraSettings(2, 4, 5, 3), 3);
        Shot shot1 = new Shot(1, cam0, pres);
        Shot shot2 = new Shot(2, cam1, pres2);
        Shot shot3 = new Shot(3, cam0, pres3);
        List<Shot> los = new ArrayList<>();
        los.add(shot1);
        los.add(shot2);
        los.add(shot3);
        Script script1 = new Script(los);
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
        Camera cam0 = new Camera();
        Preset pres = new InstantPreset(new CameraSettings(1, 1, 1, 2), 1);
        Shot shot1 = new Shot(1, cam0, pres);
        List<Shot> los2 = new ArrayList<>();
        List<Shot> los1 = new ArrayList<>();
        los1.add(shot1);
        Script script1 = new Script(los1);
        Script script2 = new Script(los2);
        assertFalse(script2.hasNext());
        assertTrue(script1.hasNext());
    }

    /**
     * Test next method.
     * Retrieves and gives the next element of a list.
     * Throws an exception is list is empty.
     */
    @Test
    public  void testNext() throws NoSuchElementException {
        Camera cam0 = new Camera();
        Preset pres = new InstantPreset(new CameraSettings(1, 1, 1, 2), 1);
        Shot shot1 = new Shot(1, cam0, pres);
        List<Shot> los2 = new ArrayList<>();
        List<Shot> los1 = new ArrayList<>();
        los1.add(shot1);
        Script script1 = new Script(los1);
        Script script2 = new Script(los2);
        assertEquals(script1.next(), shot1);
        assertFalse(script2.hasNext());
        assertFalse(script1.hasNext());
    }

}
