package test.java.nl.tudelft.contextproject.script;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import main.java.nl.tudelft.contextproject.camera.Camera;
import main.java.nl.tudelft.contextproject.camera.CameraSettings;
import main.java.nl.tudelft.contextproject.presets.InstantPreset;
import main.java.nl.tudelft.contextproject.presets.Preset;
import main.java.nl.tudelft.contextproject.script.Shot;
import main.java.nl.tudelft.contextproject.script.Timeline;

import org.junit.After;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to test the Timeline Class.Test cases will be expanded
 * with time.
 * @author Etta Tabe Takang Kajikaw
 * @since 0.2
 */
public class TimelineTest {
    
    @After
    public void cleanUp() {
        Camera.clearAllCameras();
    }

    /**
     * Test the constructor of the Timeline class.
     * Also tests addition of shots.
     */
    @Test
    public void testTimeline() {
        Camera cam0 = new Camera();
        Camera cam1 = new Camera();
        Camera cam2 = new Camera();
        Preset pres = new InstantPreset(new CameraSettings(1, 1, 1, 2), 1);
        Shot shot1 = new Shot(1, cam0, pres);
        Shot shot2 = new Shot(2, cam1, pres);
        List<Shot> los = new ArrayList<>();
        los.add(shot1);
        los.add(shot2);
        Timeline timeline1 = new Timeline(cam0, los);
        Timeline timeline2 = new Timeline(cam1, los);
        assertEquals(timeline1.getCamera(), cam0);
        assertEquals(timeline2.getCamera(), cam1);
        assertNotNull(timeline1);
        assertNotNull(timeline2);
        assertNotEquals(timeline1, timeline2);
        assertNotEquals(timeline1.getCamera(), cam1);
        timeline1.setCamera(cam2);
        assertEquals(timeline1.getCamera(), cam2);
    }

    /**
     * Test addShot method.
     * Checks if addition of a shot to our list of shots
     * is performed without problems.
     */
    @Test
    public void testAddShot() {
        List<Shot> los = new ArrayList<>();
        Camera cam0 = new Camera();
        Preset pres = new InstantPreset(new CameraSettings(1, 1, 1, 2), 1);
        Shot shot1 = new Shot(1, cam0, pres);
        Shot shot2 = new Shot(2, cam0, pres);
        Timeline timeline1 = new Timeline(cam0, los);
        timeline1.addShot(shot1);
        timeline1.addShot(shot2);
        assertEquals(timeline1.getShots().get(0), shot1);
        assertEquals(timeline1.getShots().get(1), shot2);
    }

}
