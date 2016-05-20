package nl.tudelft.contextproject.camera;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
