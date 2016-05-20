package nl.tudelft.contextproject.camera;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import nl.tudelft.contextproject.presets.InstantPreset;
import nl.tudelft.contextproject.presets.Preset;
import org.junit.After;
import org.junit.Test;

import java.util.Observable;
import java.util.Observer;

/**
 * Class to test the Camera class. More tests should be
 * added.
 * @since 0.4
 */
public class CameraTest {
    
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
        TestObserver testOb = new TestObserver();
        cam.addObserver(testOb);
        cam.setSettings(new CameraSettings(65, 65, 65, 65));
        assertEquals(65, cam.getSettings().getPan());
        assertEquals(65, cam.getSettings().getTilt());
        assertEquals(65, cam.getSettings().getZoom());
        assertEquals(1365, cam.getSettings().getFocus());
        assertTrue(testOb.wasCalled());
    }
    
    /**
     * Tests whether setSettings method actually keeps to bounds. 
     * Also tests if observer is actually called.
     */
    @Test
    public void testSetSettingsBounds() {
        Camera cam = new Camera(new CameraSettings(65, 65, 65, 65));
        TestObserver testOb = new TestObserver();
        cam.addObserver(testOb);
        cam.setSettings(new CameraSettings(-65, -65, -65, -65));
        assertEquals(0, cam.getSettings().getPan());
        assertEquals(0, cam.getSettings().getTilt());
        assertEquals(0, cam.getSettings().getZoom());
        assertEquals(1365, cam.getSettings().getFocus());
        assertTrue(testOb.wasCalled());
        cam.setSettings(new CameraSettings(656565, 656565, 656565, 656565));
        assertEquals(CameraSettings.PAN_LIMIT_HIGH, cam.getSettings().getPan());
        assertEquals(CameraSettings.TILT_LIMIT_HIGH, cam.getSettings().getTilt());
        assertEquals(CameraSettings.ZOOM_LIMIT_HIGH, cam.getSettings().getZoom());
        assertEquals(CameraSettings.FOCUS_LIMIT_HIGH, cam.getSettings().getFocus());
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
     * Tests whether pan method actually keeps to bounds.
     * Also tests if observer is actually called.
     */
    @Test
    public void testPanBounds() {
        Camera cam = new Camera();
        TestObserver testOb = new TestObserver();
        cam.addObserver(testOb);
        cam.pan(-65);
        assertEquals(0, cam.getSettings().getPan());
        assertTrue(testOb.wasCalled());
        cam.pan(656565);
        assertEquals(CameraSettings.PAN_LIMIT_HIGH, cam.getSettings().getPan());
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
     * Tests whether tilt method actually keeps to bounds.
     * Also tests if observer is actually called.
     */
    @Test
    public void testTiltBounds() {
        Camera cam = new Camera();
        TestObserver testOb = new TestObserver();
        cam.addObserver(testOb);
        cam.tilt(-65);
        assertEquals(0, cam.getSettings().getTilt());
        assertTrue(testOb.wasCalled());
        cam.tilt(656565);
        assertEquals(CameraSettings.TILT_LIMIT_HIGH, cam.getSettings().getTilt());
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
     * Tests whether zoom method actually keeps to bounds.
     * Also tests if observer is actually called.
     */
    @Test
    public void testZoomBounds() {
        Camera cam = new Camera();
        TestObserver testOb = new TestObserver();
        cam.addObserver(testOb);
        cam.zoom(-65);
        assertEquals(0, cam.getSettings().getZoom());
        assertTrue(testOb.wasCalled());
        cam.zoom(656565);
        assertEquals(CameraSettings.ZOOM_LIMIT_HIGH, cam.getSettings().getZoom());
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
     * Test whether focus actually keeps to bounds.
     * Also tests if observer is actually called.
     */
    @Test
    public void testFocusBounds() {
        Camera cam = new Camera();
        TestObserver testOb = new TestObserver();
        cam.addObserver(testOb);
        cam.focus(-65);
        assertEquals(1365, cam.getSettings().getFocus());
        assertTrue(testOb.wasCalled());
        cam.focus(656565);
        assertEquals(CameraSettings.FOCUS_LIMIT_HIGH, cam.getSettings().getFocus());
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
        assertTrue(cam.getAllPresets().contains(p));
        cam.removePreset(p);
        assertFalse(cam.getAllPresets().contains(p));
        assertTrue(cam.getAllPresets().contains(p2));
        cam.removePreset(p2);
        assertTrue(cam.getAllPresets().isEmpty());
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
