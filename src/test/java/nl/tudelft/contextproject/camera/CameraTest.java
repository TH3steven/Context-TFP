package test.java.nl.tudelft.contextproject.camera;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import main.java.nl.tudelft.contextproject.camera.Camera;
import main.java.nl.tudelft.contextproject.camera.CameraSettings;
import org.junit.Test;

import java.util.Observable;
import java.util.Observer;

/**
 * Class to test the Camera class. More tests should be
 * added.
 * 
 * @author Bart van Oort
 *
 */
public class CameraTest {

    /**
     * Tests setSettings method. Also tests if observer
     * is actually called.
     */
    @Test
    public void testSetSettings() {
        Camera cam = new Camera();
        TestObserver testOb = new TestObserver();
        cam.addObserver(testOb);
        cam.setSettings(new CameraSettings(65, 65, 65));
        assertEquals(65, cam.getSettings().getPan());
        assertEquals(65, cam.getSettings().getTilt());
        assertEquals(65, cam.getSettings().getZoom());
        assertTrue(testOb.wasCalled());
    }

    /**
     * Tests pan method. Also tests if observer
     * is actually called.
     */
    @Test
    public void testPan() {
        Camera cam = new Camera();
        TestObserver testOb = new TestObserver();
        cam.addObserver(testOb);
        cam.pan(65);
        assertEquals(65, cam.getSettings().getPan());
        assertTrue(testOb.wasCalled());
    }

    /**
     * Tests tilt method. Also tests if observer
     * is actually called.
     */
    @Test
    public void testTilt() {
        Camera cam = new Camera();
        TestObserver testOb = new TestObserver();
        cam.addObserver(testOb);
        cam.tilt(65);
        assertEquals(65, cam.getSettings().getTilt());
        assertTrue(testOb.wasCalled());
    }

    /**
     * Tests zoom method. Also tests if observer
     * is actually called.
     */
    @Test
    public void testZoom() {
        Camera cam = new Camera();
        TestObserver testOb = new TestObserver();
        cam.addObserver(testOb);
        cam.zoom(65);
        assertEquals(65, cam.getSettings().getZoom());
        assertTrue(testOb.wasCalled());
    }

}

/**
 * Simple test observer used to see if an observer was actually
 * called.
 * 
 * @author Bart van Oort
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
