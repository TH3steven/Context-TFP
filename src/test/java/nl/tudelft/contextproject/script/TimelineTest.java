package nl.tudelft.contextproject.script;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

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

/**
 * Class to test the Timeline Class. Test cases will be expanded
 * with time.
 * @since 0.2
 */
public class TimelineTest {

    private Camera cam0;
    private Camera cam1;
    private Camera cam2;
    private Preset pres;
    private Preset pres1;
    private Timeline timeline;
    private Timeline timeline1;
    private Timeline timeline2;
    private List<Shot> los;
    private List<Shot> los1;
    private Shot shot1;
    private Shot shot2;
    private Shot shot3;

    /**
     * Initializes the above variables before each test.
     */
    @Before
    public void init() {
        cam0 = new Camera();
        cam1 = new Camera();
        cam2 = new Camera();
        cam0.setConnection(new MockedCameraConnection());
        cam1.setConnection(new MockedCameraConnection());
        cam2.setConnection(new MockedCameraConnection());
        
        pres = new InstantPreset(new CameraSettings(1, 1, 1, 2), 1);
        pres1 = new InstantPreset(new CameraSettings(1, 1, 1, 3), 2);
        shot1 = new Shot(1, cam0, pres);
        shot2 = new Shot(2, cam0, pres1);
        shot3 = new Shot(2, cam1, pres);
        los = new ArrayList<>();
        los1 = new ArrayList<>();
        los.add(shot1);
        los.add(shot2);
        los1.add(shot1);
        los1.add(shot3);
        timeline = new Timeline();
        timeline1 = new Timeline(cam0, los);
        timeline2 = new Timeline(cam1, los1);
    }

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
        los.add(shot1);
        los.add(shot3);
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
     * Tests the method to load the next preset from a given shot in the timeline.
     */
    @Test
    public void testNextPreset() {
        timeline1.nextPreset(shot1);
        assertEquals(cam0.getSettings(), new CameraSettings(1, 1, 1, 3));
    }
    
    /**
     * Tests the method to load the next preset of the camera of the timeline.
     */
    @Test
    public void testInstantNextPreset() {
        timeline1.instantNextPreset();
        assertEquals(cam0.getSettings(), new CameraSettings(1, 1, 1, 2));
        assertEquals(timeline1.getCurrent(), 0);
        cam0.tilt(20);
        assertEquals(timeline1.getCurrent(), 0);
        timeline1.instantNextPreset();
        assertEquals(cam0.getSettings(), new CameraSettings(1, 1, 1, 2));
    }
    
    /**
     * Tests the method which initializes the presets.
     */
    @Test
    public void testInitPreset() {
        timeline1.initPreset();
        assertEquals(cam0.getSettings(), new CameraSettings(1, 1, 1, 2));
    }
    
    /**
     * If no errors are thrown, the initPreset method handles an empty timeline.
     */
    @Test
    public void testInitPresetEmpty() {
        timeline.initPreset();
        assertEquals(timeline.getShots().size(), 0);
    }

    /**
     * Test addShot method.
     * Checks if addition of a shot to our list of shots
     * is performed without problems.
     */
    @Test
    public void testAddShot() {
        timeline1.addShot(shot1);
        timeline1.addShot(shot2);
        assertEquals(timeline1.getShots().get(0), shot1);
        assertEquals(timeline1.getShots().get(1), shot2);
    }

    /**
     * Tests the getNextShot method. Returns null is the timeline
     */
    @Test
    public void testGetNextShot() {
        assertEquals(timeline1.getNextShot(shot1), shot2);
        assertNull(timeline.getNextShot(shot1));
    }

    /**
     * Test the executeScript method.
     * Checks if the shots in a script are properly applied
     * with the right settings per Camera.
     */
    @Test
    public void testExecuteScript() {
        timeline1.executeScript();
        assertEquals(cam0.getSettings().getFocus(), 3);
        assertEquals(cam0.getSettings().getPan(), 1);
        assertEquals(cam0.getSettings().getTilt(), 1);
        assertEquals(cam0.getSettings().getZoom(), 1);
    }
}
