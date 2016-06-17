package nl.tudelft.contextproject.camera;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import nl.tudelft.contextproject.presets.InstantPreset;
import nl.tudelft.contextproject.presets.Preset;
import org.junit.After;
import org.junit.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

/**
 * Class to test the Camera class. More tests should be
 * added.
 * @since 0.4
 */
public class CameraTest {
    private LiveCameraConnection connection;
    
    @After
    public void cleanUp() {
        Camera.clearAllCameras();
    }

    /**
     * Tests setSettings method. Also tests if observer
     * is actually called.
     */
    @Test
    public void testSetSettings() {
        Camera cam = new Camera();
        cam.setConnection(new MockedCameraConnection());
        TestObserver testOb = new TestObserver();
        cam.addObserver(testOb);
        cam.setSettings(new CameraSettings(65, 65, 65, 65));
        assertEquals(65, cam.getSettings().getPan());
        assertEquals(65, cam.getSettings().getTilt());
        assertEquals(65, cam.getSettings().getZoom());
        assertEquals(65, cam.getSettings().getFocus());
        assertTrue(testOb.wasCalled());
    }

    /**
     * Tests getAllCameras method.
     * Also tests the getCameraAmount method.
     */
    @Test
    public void testGetAllCameras() {
        Camera cam1 = new Camera();
        Camera cam2 = new Camera();
        cam1.setSettings(new CameraSettings(65, 65, 65, 65));
        cam2.setSettings(new CameraSettings(90, 90, 90, 90));
        Collection<Camera> camCollection = Camera.getAllCameras();
        assertEquals(Camera.getCamera(0), cam1);
        assertTrue(camCollection.size() == 2);
        assertEquals(Camera.getCameraAmount(), 2);
    }

    /**
     * Tests clearAllCameras method.
     */
    @Test
    public void testClearAllCameras() {
        Camera cam1 = new Camera();
        Camera cam2 = new Camera();
        cam1.setSettings(new CameraSettings(65, 65, 65, 65));
        cam2.setSettings(new CameraSettings(90, 90, 90, 90));
        Camera.clearAllCameras();
        assertTrue(Camera.getAllCameras().size() == 0);
    }

    /**
     * Tests the setConnection method.
     */
    @Test
    public  void testSetConnection() {
        connection = new LiveCameraConnection("192.168.10.101");
        Camera cam1 = new Camera();
        cam1.setSettings(new CameraSettings(65, 65, 65, 65));
        cam1.setConnection(connection);
        assertTrue(cam1.hasConnection());
        assertEquals(cam1.getConnection(), connection);
    }

    /**
     * Tests pan method. Also tests if observer
     * is actually called.
     */
    @Test
    public void testPan() {
        Camera cam = new Camera(new CameraSettings(1965, 0, 0, 0));
        TestObserver testOb = new TestObserver();
        cam.addObserver(testOb);
        cam.pan(-65);
        assertEquals(1900, cam.getSettings().getPan());
        assertTrue(testOb.wasCalled());
    }

    /**
     * Tests tilt method. Also tests if observer
     * is actually called.
     */
    @Test
    public void testTilt() {
        Camera cam = new Camera(new CameraSettings(0, 1965, 0, 0));
        TestObserver testOb = new TestObserver();
        cam.addObserver(testOb);
        cam.tilt(-65);
        assertEquals(1900, cam.getSettings().getTilt());
        assertTrue(testOb.wasCalled());
    }

    /**
     * Tests zoom method. Also tests if observer
     * is actually called.
     */
    @Test
    public void testZoom() {
        Camera cam = new Camera(new CameraSettings(0, 0, 65, 0));
        TestObserver testOb = new TestObserver();
        cam.addObserver(testOb);
        cam.zoom(-42);
        assertEquals(23, cam.getSettings().getZoom());
        assertTrue(testOb.wasCalled());
    }

    /**
     * Tests zoom method. Also tests if observer
     * is actually called.
     */
    @Test
    public void testFocus() {
        Camera cam = new Camera(new CameraSettings(0, 0, 0, 1610));
        TestObserver testOb = new TestObserver();
        cam.addObserver(testOb);
        cam.focus(-42);
        assertEquals(1568, cam.getSettings().getFocus());
        assertTrue(testOb.wasCalled());
    }

    /**
     * Tests absPanTilt method.
     * Also tests if the observer is actually called.
     */
    @Test
    public void testAbsPanTilt() {
        Camera cam = new Camera(new CameraSettings(30, 30, 0, 0));
        TestObserver testOb = new TestObserver();
        cam.addObserver(testOb);
        cam.absPanTilt(45, 45);
        assertEquals(45, cam.getSettings().getPan());
        assertEquals(45, cam.getSettings().getTilt());
        assertTrue(testOb.wasCalled());
    }

    /**
     * Tests the absPan method.
     * Also tests if the observer is actually called.
     */
    @Test
    public void testAbsPan() {
        Camera cam = new Camera(new CameraSettings(30, 0, 0, 0));
        TestObserver testOb = new TestObserver();
        cam.addObserver(testOb);
        cam.absPan(45);
        assertEquals(45, cam.getSettings().getPan());
        assertTrue(testOb.wasCalled());
    }

    /**
     * Tests the panTilt method.
     * Also tests if the observer is actually called.
     */
    @Test
    public void testPanTilt() {
        Camera cam = new Camera(new CameraSettings(30, 30, 30, 30));
        TestObserver testOb = new TestObserver();
        cam.addObserver(testOb);
        cam.panTilt(30, 30);
        assertEquals(60, cam.getSettings().getPan());
        assertEquals(60, cam.getSettings().getTilt());
        assertTrue(testOb.wasCalled());
    }

    /**
     * Tests the absTilt method.
     * Also tests if the observer is actually called.
     */
    @Test
    public void testAbsTilt() {
        Camera cam = new Camera(new CameraSettings(0, 30, 0, 0));
        TestObserver testOb = new TestObserver();
        cam.addObserver(testOb);
        cam.absTilt(45);
        assertEquals(45, cam.getSettings().getTilt());
        assertTrue(testOb.wasCalled());
    }

    /**
     * Test the absZoom method.
     * Also tests if the observer is called.
     */
    @Test
    public void testAbsZoom() {
        Camera cam = new Camera(new CameraSettings(0, 0, 30, 0));
        TestObserver testOb = new TestObserver();
        cam.addObserver(testOb);
        cam.absZoom(45);
        assertEquals(45, cam.getSettings().getZoom());
        assertTrue(testOb.wasCalled());
    }

    /**
     * Tests the addPreset method.
     */
    @Test
    public void testAddPreset() {
        Camera cam = new Camera(new CameraSettings(1, 33, 7, 10));
        Preset p = new InstantPreset(new CameraSettings(65, 65, 65, 65), 1);
        cam.addPreset(p);
        assertTrue(cam.getPresets().containsKey(p.getId()));
    }

    /**
     * Tests the overwritePreset method.
     */
    @Test
    public void testOverwritePreset() {
        Camera cam = new Camera(new CameraSettings(1, 33, 7, 10));
        Preset p = new InstantPreset(new CameraSettings(65, 65, 65, 65), 1);
        p.getToSet().setPan(130);
        cam.addPreset(p);
        cam.overwritePreset(p);
        assertEquals(cam.getPreset(1).getToSet().getPan(), 130);
        assertEquals(cam.getPreset(1).getToSet().getTilt(), 65);
    }

    /**
     * Test the removePreset method.
     */
    @Test
    public void testRemovePreset() {
        Camera cam = new Camera(new CameraSettings(1, 10, 10, 10));
        Preset p = new InstantPreset(new CameraSettings(65, 65, 65, 65), 1);
        Preset p2 = new InstantPreset(new CameraSettings(35, 35, 35, 30), 2);
        cam.addPreset(p);
        cam.addPreset(p2);
        assertEquals(cam.getPreset(1), p);
        cam.removePreset(p);
        assertEquals(cam.getPreset(2), p2);
        cam.removePreset(p2);
        assertTrue(cam.getPresets().isEmpty());
    }

    /**
     * Tests the getAllPresets method.
     * Also tests the getPresetAmount method.
     */
    @Test
    public void testGetAllPresets() {
        Camera cam = new Camera(new CameraSettings(1, 10, 10, 10));
        Preset p = new InstantPreset(new CameraSettings(65, 65, 65, 65), 1);
        Preset p2 = new InstantPreset(new CameraSettings(35, 35, 35, 30), 2);
        cam.addPreset(p);
        cam.addPreset(p2);
        HashMap<Integer, Preset> presetCollection = new HashMap<Integer, Preset>();
        presetCollection.put(1, p);
        presetCollection.put(2, p2);
        assertTrue(cam.getAllPresets().containsAll(presetCollection.values()));
        assertTrue(cam.getAllPresets().size() == presetCollection.values().size());
        assertEquals(cam.getPresetAmount(), 2);
    }

    /**
     * Tests the toString method.
     */
    @Test
    public void testToString() {
        Camera cam = new Camera(new CameraSettings(10, 10, 10, 10));
        String expected = "Camera: " +cam.getNumber();
        assertEquals(expected, cam.toString());
    }

    /**
     * Simple test observer used to see if an observer was actually
     * called.
     */
    class TestObserver implements Observer {
        boolean called = false;

        @Override
        public void update(Observable o, Object arg) {
            called = true;
        }
    
        public boolean wasCalled() {
            return called;
        }
    }
}
