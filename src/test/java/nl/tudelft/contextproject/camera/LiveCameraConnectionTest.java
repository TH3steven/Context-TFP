package nl.tudelft.contextproject.camera;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

public class LiveCameraConnectionTest {
    
    private static boolean doTests = true;
    private static final int MAX_MOV_OFFSET = 5;
    
    private LiveCameraConnection connection;
    
    @Before
    public void setUp() throws Exception {
        connection = new LiveCameraConnection("192.168.10.101");
        doTests = doTests ? connection.setUpConnection() : doTests;
    }

    @Test
    public void testSetUpConnection() {
        if (doTests) {
            assertTrue(connection.isConnected());
            assertTrue(connection.hasAutoFocus());
        }
    }

    @Test
    public void testGetCurrentCameraSettings() {
        if (doTests) {
            CameraSettings curSet = connection.getCurrentCameraSettings();
            assertNotNull(curSet);
            assertEquals(curSet, connection.getLastKnownSettings());
        }
    }

    @Test
    public void testAbsPanTilt() throws InterruptedException {
        if (doTests) {
            assertTrue(connection.absPanTilt(29965, 28965));
            Thread.sleep(4000);
            int[] curSet = connection.getCurrentPanTilt();
            assertWithinMaxOffset(29965, curSet[0]);
            assertWithinMaxOffset(28965, curSet[1]);
        }
    }

    @Test
    public void testAbsPan() throws InterruptedException {
        if (doTests) {
            int[] before = connection.getCurrentPanTilt();
            assertTrue(connection.absPan(31965));
            Thread.sleep(4000);
            int[] after = connection.getCurrentPanTilt();
            assertWithinMaxOffset(before[1], after[1]);
            assertWithinMaxOffset(31965, after[0]);
        }
    }

    @Test
    public void testAbsTilt() throws InterruptedException {
        if (doTests) {
            int[] before = connection.getCurrentPanTilt();
            assertTrue(connection.absTilt(31965));
            Thread.sleep(4000);
            int[] after = connection.getCurrentPanTilt();
            assertWithinMaxOffset(before[0], after[0]);
            assertWithinMaxOffset(31965, after[1]);
        }
    }

    @Test
    public void testAbsZoom() throws InterruptedException {
        if (doTests) {
            assertTrue(connection.absZoom(1965));
            Thread.sleep(2000);
            int newZoom = connection.getCurrentZoom();
            assertWithinMaxOffset(1965, newZoom);
        }
    }

    @Test
    public void testAbsFocusNoAutoFocus() {
        if (doTests) {
            assertTrue(connection.setAutoFocus(false));
            assertTrue(connection.absFocus(1965));
            int newFocus = connection.getCurrentFocus();
            assertWithinMaxOffset(1965, newFocus);
        }
    }
    
    @Test
    public void testAbsFocusWithAutoFocus() {
        if (doTests) {
            assertTrue(connection.setAutoFocus(true));
            assertFalse(connection.absFocus(1965));
            int newFocus = connection.getCurrentFocus();
            assertEquals(-1, newFocus);
        }
    }

    @Test
    public void testRelPanTilt() throws InterruptedException {
        if (doTests) {
            int[] before = connection.getCurrentPanTilt();
            assertTrue(connection.relPanTilt(-420, 420));
            Thread.sleep(2000);
            int[] after = connection.getCurrentPanTilt();
            assertWithinMaxOffset(before[0] - 420, after[0]);
            assertWithinMaxOffset(before[1] + 420, after[1]);
        }
    }

    @Test
    public void testRelPan() throws InterruptedException {
        if (doTests) {
            int[] before = connection.getCurrentPanTilt();
            assertTrue(connection.relPan(420));
            Thread.sleep(2000);
            int[] after = connection.getCurrentPanTilt();
            assertWithinMaxOffset(before[0] + 420, after[0]);
            assertWithinMaxOffset(before[1], after[1]);
        }
    }

    @Test
    public void testRelTilt() throws InterruptedException {
        if (doTests) {
            int[] before = connection.getCurrentPanTilt();
            assertTrue(connection.relTilt(420));
            Thread.sleep(2000);
            int[] after = connection.getCurrentPanTilt();
            assertWithinMaxOffset(before[0], after[0]);
            assertWithinMaxOffset(before[1] + 420, after[1]);
        }
    }

    @Test
    public void testRelZoom() throws InterruptedException {
        if (doTests) {
            int oldZoom = connection.getCurrentZoom();
            assertTrue(connection.relZoom(65));
            Thread.sleep(1000);
            int newZoom = connection.getCurrentZoom();
            assertWithinMaxOffset(oldZoom + 65, newZoom);
        }
    }

    @Test
    public void testRelFocus() throws InterruptedException {
        if (doTests) {
            assertTrue(connection.setAutoFocus(false));
            int oldFocus = connection.getCurrentFocus();
            assertTrue(connection.relFocus(65));
            Thread.sleep(1000);
            int newFocus = connection.getCurrentFocus();
            assertWithinMaxOffset(oldFocus + 65, newFocus);
        }
    }

    @Test
    public void testSetAutoFocus() {
        if (doTests) {
            boolean currentAutoFocus = connection.hasAutoFocus();
            connection.setAutoFocus(!currentAutoFocus);
            assertEquals(!currentAutoFocus, connection.hasAutoFocus());
        }
    }

    @Test
    public void testUpdateAll() {
        Camera c = new Camera();
        connection = spy(new LiveCameraConnection("192.168.10.101"));
        doReturn(new CameraSettings(0, 0, 0, 0)).when(connection).getCurrentCameraSettings();
        doReturn(true).when(connection).absPanTilt(1965, 65);
        doReturn(true).when(connection).absZoom(650);
        doReturn(true).when(connection).absFocus(6500);
        connection.update(c, new CameraSettings(1965, 65, 650, 6500));
        verify(connection).absPanTilt(1965, 65);
        verify(connection).absZoom(650);
        verify(connection).absFocus(6500);
        Camera.clearAllCameras();
    }
    
    @Test
    public void testUpdatePanTiltOnly() {
        Camera c = new Camera();
        connection = spy(new LiveCameraConnection("192.168.10.101"));
        doReturn(new CameraSettings(0, 0, 0, 0)).when(connection).getCurrentCameraSettings();
        doReturn(true).when(connection).absPanTilt(1965, 65);
        connection.update(c, new CameraSettings(1965, 65, 0, 0));
        verify(connection).absPanTilt(1965, 65);
        verify(connection, never()).absZoom(anyInt());
        verify(connection, never()).absFocus(anyInt());
        Camera.clearAllCameras();
    }
    
    @Test
    public void testUpdateZoomOnly() {
        Camera c = new Camera();
        connection = spy(new LiveCameraConnection("192.168.10.101"));
        doReturn(new CameraSettings(0, 0, 0, 0)).when(connection).getCurrentCameraSettings();
        doReturn(true).when(connection).absZoom(1965);
        connection.update(c, new CameraSettings(0, 0, 1965, 0));
        verify(connection, never()).absPanTilt(anyInt(), anyInt());
        verify(connection).absZoom(1965);
        verify(connection, never()).absFocus(anyInt());
        Camera.clearAllCameras();
    }
    
    @Test
    public void testUpdateFocusOnly() {
        Camera c = new Camera();
        connection = spy(new LiveCameraConnection("192.168.10.101"));
        doReturn(new CameraSettings(0, 0, 0, 0)).when(connection).getCurrentCameraSettings();
        doReturn(true).when(connection).absFocus(1965);
        connection.update(c, new CameraSettings(0, 0, 0, 1965));
        verify(connection, never()).absPanTilt(anyInt(), anyInt());
        verify(connection, never()).absZoom(anyInt());
        verify(connection).absFocus(1965);
        Camera.clearAllCameras();
    }
    
    /**
     * Method to assert that the actual value is within {@link #MAX_MOV_OFFSET}
     * from the expected value.
     * @param expected expected value.
     * @param actual actual value.
     */
    public static void assertWithinMaxOffset(int expected, int actual) {
        int low = expected - MAX_MOV_OFFSET;
        int high = expected + MAX_MOV_OFFSET;
        if (actual < low || actual > high) {
            fail("Expected between <" + low + "> and <" + high + "> but was <" + actual + ">");
        }
    }

}
