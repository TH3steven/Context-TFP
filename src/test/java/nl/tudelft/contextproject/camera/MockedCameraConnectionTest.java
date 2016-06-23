package nl.tudelft.contextproject.camera;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

/**
 * Class to test the behavior of a mocked camera connection.
 * @since 0.4
 */
public class MockedCameraConnectionTest {
    private MockedCameraConnection mockedCam;
    private CameraSettings camSet;
    private Camera cam1;


    @After
    public void cleanUp() {
        Camera.clearAllCameras();
    }

    /**
     * Set up of the tests.
     */
    @Before
    public void setUp() {
        camSet = new CameraSettings(30, 30, 30, 1365);
        mockedCam = new MockedCameraConnection();
        cam1 = new Camera();
    }

    /**
     * Tests getCurrentCameraSettings method.
     */
    @Test
    public void testGetCurrentCameraSettings() {
        assertEquals(mockedCam.getCurrentCameraSettings(), camSet);
    }

    /**
     * Tests the setUpConnection method to check if the connection
     * is set up properly.
     */
    @Test
    public void testSetUpConnection() {
        mockedCam.setUpConnection();
        assertTrue(mockedCam.isConnected());
    }

    /**
     * Tests the getStreamLink method. This tests if the
     * correct streamLink for the cam is gotten.
     */
    @Test
    public void testGetStreamLink() {
        String streamLink = "http://qthttp.apple.com.edgesuite.net/1010qwoeiuryfg/sl.m3u8";
        assertTrue(mockedCam.getStreamLink().equals(streamLink));
    }

    /**
     * Tests the getCurrentZoom method.
     */
    @Test
    public void testGetCurrentZoom() {
        cam1.setConnection(mockedCam);
        cam1.setSettings(camSet);
        assertEquals(mockedCam.getCurrentZoom(), camSet.getZoom());
    }

    /**
     * Tests the getCurrentFocus method.
     */
    @Test
    public void testGetCurrentFocus() {
        cam1.setConnection(mockedCam);
        cam1.setSettings(camSet);
        assertEquals(mockedCam.getCurrentFocus(), camSet.getFocus());
    }

    /**
     * Tests update method.
     */
    @Test
    public void testUpdate() {
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
        CameraSettings camSet2 = new CameraSettings(60, 30, 30, 1365);
        cam1.setConnection(mockedCam);
        cam1.absPan(60);
        assertEquals(mockedCam.getCurrentCameraSettings(), camSet2);

    }

    /**
     * Tests absTilt method. Checks if the correct cameraSettings are returned
     * after an absolute tilt is performed.
     */
    @Test
    public void testAbsTilt() {
        CameraSettings camSet2 = new CameraSettings(30, 90, 30, 1365);
        cam1.setConnection(mockedCam);
        cam1.absTilt(90);
        assertEquals(mockedCam.getCurrentCameraSettings(), camSet2);
    }

    /**
     * Tests absZoom method. Checks if the correct cameraSettings are returned
     * after an absolute zoom is performed.
     */
    @Test
    public void testAbsZoom() {
        CameraSettings camSet2 = new CameraSettings(30, 30, 60, 1365);
        cam1.setConnection(mockedCam);
        cam1.absZoom(60);
        assertEquals(mockedCam.getCurrentCameraSettings(), camSet2);
    }

    /**
     * Tests absPan method. Checks if the correct cameraSettings are returned
     * after an absolute Focus is performed.
     */
    @Test
    public void testAbsFocus() {
        CameraSettings camSet2 = new CameraSettings(30, 30, 30, 1800);
        cam1.setConnection(mockedCam);
        cam1.absFocus(1800);
        assertEquals(mockedCam.getCurrentCameraSettings(), camSet2);
    }

    /**
     * Tests relPan method. Checks if the correct cameraSettings are returned
     * after a relative pan is performed.
     */
    @Test
    public void testRelPan() {
        CameraSettings camSet2 = new CameraSettings(90, 30, 30, 1365);
        cam1.setConnection(mockedCam);
        cam1.pan(60);
        assertEquals(mockedCam.getCurrentCameraSettings(), camSet2);
    }

    /**
     * Tests relTilt method. Checks if the correct cameraSettings are returned
     * after a relative tilt is performed.
     */
    @Test
    public void testRelTilt() {
        CameraSettings camSet2 = new CameraSettings(30, 90, 30, 1365);
        cam1.setConnection(mockedCam);
        cam1.tilt(60);
        assertEquals(mockedCam.getCurrentCameraSettings(), camSet2);
    }

    /**
     * Tests relZoom method. Checks if the correct cameraSettings are returned
     * after a relative zoom is performed.
     */
    @Test
    public void testRelZoom() {
        CameraSettings camSet2 = new CameraSettings(30, 30, 90, 1365);
        cam1.setConnection(mockedCam);
        cam1.zoom(60);
        assertEquals(mockedCam.getCurrentCameraSettings(), camSet2);
    }

    /**
     * Tests relFocus method. Checks if the correct cameraSettings are returned
     * after a relative focus is performed.
     */
    @Test
    public void testRelFocus() {
        CameraSettings camSet2 = new CameraSettings(30, 30, 30, 1425);
        cam1.setConnection(mockedCam);
        cam1.focus(60);
        assertEquals(mockedCam.getCurrentCameraSettings(), camSet2);
    }

    /**
     * Tests relPanTilt method. Checks if the correct cameraSettings are returned
     * after a relative pan and tilt are performed simultaneously.
     */
    @Test
    public void testRelPanTilt() {
        CameraSettings camSet2 = new CameraSettings(60, 60, 30, 1365);
        cam1.setConnection(mockedCam);
        cam1.panTilt(30, 30);
        int[] panTilt;
        panTilt = new int[]{60, 60};
        assertEquals(mockedCam.getCurrentCameraSettings(), camSet2);
        Arrays.equals(panTilt, mockedCam.getCurrentPanTilt());
    }
}

