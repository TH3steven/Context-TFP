package test.java.nl.tudelft.contextproject.camera;

import main.java.nl.tudelft.contextproject.camera.Camera;
import main.java.nl.tudelft.contextproject.camera.CameraSettings;
import main.java.nl.tudelft.contextproject.camera.MockedCameraConnection;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


/**
 * Class to test the behavior of a mimiced or mocked camera.
 * @since 0.4
 */
public class MockedCameraConnectionTest {

    @After
    public void cleanUp() {
        Camera.clearAllCameras();
    }

    /**
     * Tests getCurrentCameraSettings method.
     */
    @Test
    public void testGetCurrentCameraSettings() {
        MockedCameraConnection mockedCam = new MockedCameraConnection();
        CameraSettings camSet = new CameraSettings(30, 30, 30, 1365);
        assertEquals(mockedCam.getCurrentCameraSettings(), camSet);
    }
    /**
     * Tests update method.
     */
    @Test
    public void testUpdate() {
        Camera cam1 = new Camera();
        MockedCameraConnection mockedCam = new MockedCameraConnection();
        cam1.setConnection(mockedCam);
        CameraSettings camSet = new CameraSettings(60, 60, 60, 1365);
        cam1.setSettings(camSet);
        assertEquals(mockedCam.getCurrentCameraSettings(), camSet);
    }

    /**
     * Test AbsPanTilt Method. Checks if this method
     * gets the correct cameraSettings when the pan and tilt are changed.
     */
    @Test
    public void testAbsPanTilt() {
        Camera cam1 = new Camera();
        MockedCameraConnection mockedCam = new MockedCameraConnection();
        CameraSettings camSet2 = new CameraSettings(45, 45, 30, 1365);
        cam1.setConnection(mockedCam);
        cam1.absPanTilt(45, 45);
        assertEquals(mockedCam.getCurrentCameraSettings(), camSet2);

    }

    /**
     * Tests absPan method. Checks if the correct cameraSettings are returned
     * after an absolute pan is performed.
     */
    @Test
    public void testAbsPan() {
        Camera cam = new Camera();
        MockedCameraConnection mockedCam = new MockedCameraConnection();
        CameraSettings camSet2 = new CameraSettings(60, 30, 30, 1365);
        cam.setConnection(mockedCam);
        cam.absPan(60);
        assertEquals(mockedCam.getCurrentCameraSettings(), camSet2);

    }

    /**
     * Tests absTilt method. Checks if the correct cameraSettings are returned
     * after an absolute tilt is performed.
     */
    @Test
    public void testAbsTilt() {
        Camera cam = new Camera();
        MockedCameraConnection mockedCam = new MockedCameraConnection();
        CameraSettings camSet2 = new CameraSettings(30, 90, 30, 1365);
        cam.setConnection(mockedCam);
        cam.absTilt(90);
        assertEquals(mockedCam.getCurrentCameraSettings(), camSet2);
    }

    /**
     * Tests absZoom method. Checks if the correct cameraSettings are returned
     * after an absolute zoom is performed.
     */
    @Test
    public void testAbsZoom() {
        Camera cam = new Camera();
        MockedCameraConnection mockedCam = new MockedCameraConnection();
        CameraSettings camSet2 = new CameraSettings(30, 30, 60, 1365);
        cam.setConnection(mockedCam);
        cam.absZoom(60);
        assertEquals(mockedCam.getCurrentCameraSettings(), camSet2);
    }

    /**
     * Tests absPan method. Checks if the correct cameraSettings are returned
     * after an absolute Focus is performed.
     */
    @Test
    public void testAbsFocus() {
        Camera cam = new Camera();
        MockedCameraConnection mockedCam = new MockedCameraConnection();
        CameraSettings camSet2 = new CameraSettings(30, 30, 30, 1800);
        cam.setConnection(mockedCam);
        cam.absFocus(1800);
        assertEquals(mockedCam.getCurrentCameraSettings(), camSet2);
    }

    /**
     * Tests relPan method. Checks if the correct cameraSettings are returned
     * after a relative pan is performed.
     */
    @Test
    public void testRelPan() {
        Camera cam = new Camera();
        MockedCameraConnection mockedCam = new MockedCameraConnection();
        CameraSettings camSet2 = new CameraSettings(90, 30, 30, 1365);
        cam.setConnection(mockedCam);
        cam.pan(60);
        assertEquals(mockedCam.getCurrentCameraSettings(), camSet2);
    }

    /**
     * Tests relTilt method. Checks if the correct cameraSettings are returned
     * after a relative tilt is performed.
     */
    @Test
    public void testRelTilt() {
        Camera cam = new Camera();
        MockedCameraConnection mockedCam = new MockedCameraConnection();
        CameraSettings camSet2 = new CameraSettings(30, 90, 30, 1365);
        cam.setConnection(mockedCam);
        cam.tilt(60);
        assertEquals(mockedCam.getCurrentCameraSettings(), camSet2);
    }

    /**
     * Tests relZoom method. Checks if the correct cameraSettings are returned
     * after a relative zoom is performed.
     */
    @Test
    public void testRelZoom() {
        Camera cam = new Camera();
        MockedCameraConnection mockedCam = new MockedCameraConnection();
        CameraSettings camSet2 = new CameraSettings(30, 30, 90, 1365);
        cam.setConnection(mockedCam);
        cam.zoom(60);
        assertEquals(mockedCam.getCurrentCameraSettings(), camSet2);
    }

    /**
     * Tests relFocus method. Checks if the correct cameraSettings are returned
     * after a relative focus is performed.
     */
    @Test
    public void testRelFocus() {
        Camera cam = new Camera();
        MockedCameraConnection mockedCam = new MockedCameraConnection();
        CameraSettings camSet2 = new CameraSettings(30, 30, 30, 1425);
        cam.setConnection(mockedCam);
        cam.focus(60);
        assertEquals(mockedCam.getCurrentCameraSettings(), camSet2);
    }

}
