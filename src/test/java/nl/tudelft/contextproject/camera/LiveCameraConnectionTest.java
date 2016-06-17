package nl.tudelft.contextproject.camera;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.spy;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.net.URL;

/**
 * Class to test LiveCameraConnection. It will first check if it
 * can be run with a real connection to the camera with the specified IP.
 * If it cannot, then it will use PowerMock to mock the 
 * {@link LiveCameraConnection#sendRequest(URL)} method.
 * 
 * <p>Uses @SuppressWarnings for some PMD warnings. Using JUnit and
 * PowerMock(ito) brings a lot of static imports, but they do not decrease
 * the readability of the code. The duplicate literals warning is of no
 * interest to this class.
 * 
 * @since 0.4, modified heavily for 0.7
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(LiveCameraConnection.class)
@SuppressWarnings({"PMD.AvoidDuplicateLiterals", "PMD.TooManyStaticImports"})
public class LiveCameraConnectionTest {
    
    private static boolean testLive;
    private static final int MAX_MOV_OFFSET = 5;
    private static final String CAMERA_IP = "192.168.10.101";
    
    private static LiveCameraConnection connection;
    
    @BeforeClass
    public static void setUpClass() throws Exception {
        connection = new LiveCameraConnection(CAMERA_IP);
        testLive = connection.setUpConnection();
    }
    
    /**
     * Sets up the connection for testing. Uses a partial PowerMockito mock
     * for the connection. Sets some standard mocking rules for the 
     * {@link LiveCameraConnection#sendRequest(URL)} method if there is no
     * live connection.
     * 
     * @throws Exception See {@link PowerMockito#when(Object, String, Object...)}
     */
    @Before
    public void setUpTest() throws Exception {
        connection = spy(new LiveCameraConnection(CAMERA_IP));
        if (!testLive) {
            URL cameraModelURL = connection.buildCamControlURL("QID");
            URL autoFocusURL = connection.buildPanTiltHeadControlURL("%23D1");
            URL absPanTiltURL = connection.buildPanTiltHeadControlURL("%23APC");
            URL zoomURL = connection.buildPanTiltHeadControlURL("%23GZ");
            URL focusURL = connection.buildPanTiltHeadControlURL("%23GF");
            URL autoFocusOffURL = connection.buildPanTiltHeadControlURL("%23D10");
            URL autoFocusOnURL = connection.buildPanTiltHeadControlURL("%23D11");
            doReturn("OID:AW-HE130").when(connection, "sendRequest", cameraModelURL);
            doReturn("d11").when(connection, "sendRequest", autoFocusURL);
            doReturn("aPC80008000").when(connection, "sendRequest", absPanTiltURL);
            doReturn("gz555").when(connection, "sendRequest", zoomURL);
            doReturn("gf555").when(connection, "sendRequest", focusURL);
            doReturn("d10").when(connection, "sendRequest", autoFocusOffURL);
            doReturn("d11").when(connection, "sendRequest", autoFocusOnURL);
        }
        connection.setUpConnection();
    }

    /**
     * Tests whether the connection was set up properly.
     * @throws Exception See {@link PowerMockito#when(Object, String, Object...)}
     */
    @Test
    public void testSetUpConnection() throws Exception {
        if (!testLive) {
            connection.setUpConnection();
            assertEquals(new CameraSettings(32768, 32768, 1365, -1), connection.getLastKnownSettings());
        }
        assertTrue(connection.isConnected());
        assertTrue(connection.hasAutoFocus());
    }

    /**
     * Tests whether the getCurrentCameraSettings returns something
     * and updates the last known settings properly.
     */
    @Test
    public void testGetCurrentCameraSettings() {
        CameraSettings curSet = connection.getCurrentCameraSettings();
        assertNotNull(curSet);
        assertEquals(curSet, connection.getLastKnownSettings());
    }

    /**
     * Tests {@link LiveCameraConnection#absPanTilt(int, int)}.
     * @throws Exception See {@link PowerMockito#when(Object, String, Object...)}
     */
    @Test
    public void testAbsPanTilt() throws Exception {
        if (!testLive) {
            URL absPanTiltURL = connection.buildPanTiltHeadControlURL("%23APS750D71251D2");
            URL curPanTiltURL = connection.buildPanTiltHeadControlURL("%23APC");
            doReturn("aPS750D71251D2").when(connection, "sendRequest", absPanTiltURL);
            doReturn("aPC750D7125").when(connection, "sendRequest", curPanTiltURL);
        }
        assertTrue(connection.absPanTilt(29965, 28965));
        if (testLive) {
            Thread.sleep(4000);
        }
        int[] curSet = connection.getCurrentPanTilt();
        assertWithinMaxOffset(29965, curSet[0]);
        assertWithinMaxOffset(28965, curSet[1]);
    }

    /**
     * Tests {@link LiveCameraConnection#absPan(int)}.
     * @throws Exception See {@link PowerMockito#when(Object, String, Object...)}
     */
    @Test
    public void testAbsPan() throws Exception {
        int[] before = connection.getCurrentPanTilt();
        if (!testLive) {
            URL absPanTiltURL = connection.buildPanTiltHeadControlURL("%23APS7CDD80001D2");
            URL getPanTiltURL = connection.buildPanTiltHeadControlURL("%23APC");
            doReturn("aPS").when(connection, "sendRequest", absPanTiltURL);
            doReturn("aPC7CDD8000").when(connection, "sendRequest", getPanTiltURL);
        }
        assertTrue(connection.absPan(31965));
        if (testLive) {
            Thread.sleep(4000);
        }
        int[] after = connection.getCurrentPanTilt();
        assertWithinMaxOffset(before[1], after[1]);
        assertWithinMaxOffset(31965, after[0]);
    }

    /**
     * Tests {@link LiveCameraConnection#absTilt(int)}.
     * @throws Exception See {@link PowerMockito#when(Object, String, Object...)}
     */
    @Test
    public void testAbsTilt() throws Exception {
        int[] before = connection.getCurrentPanTilt();
        if (!testLive) {
            URL absPanTiltURL = connection.buildPanTiltHeadControlURL("%23APS80007CDD1D2");
            URL getPanTiltURL = connection.buildPanTiltHeadControlURL("%23APC");
            doReturn("aPS").when(connection, "sendRequest", absPanTiltURL);
            doReturn("aPC80007CDD").when(connection, "sendRequest", getPanTiltURL);
        }
        assertTrue(connection.absTilt(31965));
        if (testLive) {
            Thread.sleep(4000);
        }
        int[] after = connection.getCurrentPanTilt();
        assertWithinMaxOffset(before[0], after[0]);
        assertWithinMaxOffset(31965, after[1]);
    }

    /**
     * Tests {@link LiveCameraConnection#absZoom(int)}.
     * @throws Exception See {@link PowerMockito#when(Object, String, Object...)}
     */
    @Test
    public void testAbsZoom() throws Exception {
        if (!testLive) {
            URL getZoomURL = connection.buildPanTiltHeadControlURL("%23GZ");
            URL zoomURL = connection.buildPanTiltHeadControlURL("%23AXZ7AD");
            doReturn("axz7AD").when(connection, "sendRequest", zoomURL);
            doReturn("gz7AD").when(connection, "sendRequest", getZoomURL);
        }
        assertTrue(connection.absZoom(1965));
        if (testLive) {
            Thread.sleep(2000);
        }
        int newZoom = connection.getCurrentZoom();
        assertWithinMaxOffset(1965, newZoom);
    }

    /**
     * Tests {@link LiveCameraConnection#absFocus(int)} when auto focus
     * is turned off.
     * @throws Exception See {@link PowerMockito#when(Object, String, Object...)}
     */
    @Test
    public void testAbsFocusNoAutoFocus() throws Exception {
        if (!testLive) {
            URL autoFocusOffURL = connection.buildPanTiltHeadControlURL("%23D10");
            URL focusURL = connection.buildPanTiltHeadControlURL("%23AXF7AD");
            URL getFocusURL = connection.buildPanTiltHeadControlURL("%23GF");
            doReturn("d10").when(connection, "sendRequest", autoFocusOffURL);
            doReturn("axf7AD").when(connection, "sendRequest", focusURL);
            doReturn("gf7AD").when(connection, "sendRequest", getFocusURL);
        }
        assertTrue(connection.setAutoFocus(false));
        assertTrue(connection.absFocus(1965));
        int newFocus = connection.getCurrentFocus();
        assertWithinMaxOffset(1965, newFocus);
    }
    
    /**
     * Tests {@link LiveCameraConnection#absFocus(int)} when auto focus
     * is turned on.
     */
    @Test
    public void testAbsFocusWithAutoFocus() {
        assertTrue(connection.setAutoFocus(true));
        assertFalse(connection.absFocus(1965));
        int newFocus = connection.getCurrentFocus();
        assertEquals(-1, newFocus);
    }

    /**
     * Tests {@link LiveCameraConnection#relPanTilt(int, int)}.
     * @throws Exception See {@link PowerMockito#when(Object, String, Object...)}
     */
    @Test
    public void testRelPanTilt() throws Exception {
        int[] before = connection.getCurrentPanTilt();
        if (!testLive) {
            URL relPanTiltURL = connection.buildPanTiltHeadControlURL("%23RPC7E5C81A4");
            URL getPanTiltURL = connection.buildPanTiltHeadControlURL("%23APC");
            doReturn("rPC7E5C81A4").when(connection, "sendRequest", relPanTiltURL);
            doReturn("aPC7E5C81A4").when(connection, "sendRequest", getPanTiltURL);
        }
        assertTrue(connection.relPanTilt(-420, 420));
        if (!testLive) {
            Thread.sleep(2000);
        }
        int[] after = connection.getCurrentPanTilt();
        assertWithinMaxOffset(before[0] - 420, after[0]);
        assertWithinMaxOffset(before[1] + 420, after[1]);
    }

    /**
     * Tests {@link LiveCameraConnection#relPan(int)}. Since this method
     * delegates to {@link LiveCameraConnection#relPanTilt(int, int)}, we
     * only need to check if the delegation happens correctly.
     */
    @Test
    public void testRelPan() {
        doReturn(true).when(connection).relPanTilt(420, 0);
        assertTrue(connection.relPan(420));
        verify(connection).relPanTilt(420, 0);
    }

    /**
     * Tests {@link LiveCameraConnection#relTilt(int)}. Since this method
     * delegates to {@link LiveCameraConnection#relPanTilt(int, int)}, we
     * only need to check if the delegation happens correctly.
     */
    @Test
    public void testRelTilt() {
        doReturn(true).when(connection).relPanTilt(0, 420);
        assertTrue(connection.relTilt(420));
        verify(connection).relPanTilt(0, 420);
    }

    /**
     * Tests {@link LiveCameraConnection#relZoom(int)}. Since this method
     * delegates to {@link LiveCameraConnection#absZoom(int)}, we
     * only need to check if the delegation happens correctly.
     */
    @Test
    public void testRelZoom() {
        doReturn(true).when(connection).absZoom(1430);
        assertTrue(connection.relZoom(65));
        verify(connection).absZoom(1430);
    }

    /**
     * Tests {@link LiveCameraConnection#relFocus(int)} with auto focus
     * turned off. Since that method delegates to 
     * {@link LiveCameraConnection#absFocus(int)}, we only need to check
     * if the delegation happens correctly.
     */
    @Test
    public void testRelFocus() {
        connection.setAutoFocus(false);
        doReturn(true).when(connection).absFocus(1430);
        assertTrue(connection.relFocus(65));
        verify(connection).absFocus(1430);
    }

    /**
     * Tests setAutoFocus method.
     * @throws Exception See {@link PowerMockito#when(Object, String, Object...)}
     */
    @Test
    public void testSetAutoFocus() throws Exception {
        boolean currentAutoFocus = connection.hasAutoFocus();
        if (!testLive) {
            URL autoFocusURL = connection.buildPanTiltHeadControlURL("%23D1");
            doReturn("d1" + (currentAutoFocus ? 0 : 1)).when(connection, "sendRequest", autoFocusURL);
        }
        connection.setAutoFocus(!currentAutoFocus);
        assertEquals(!currentAutoFocus, connection.hasAutoFocus());
    }

    /**
     * Tests update method:
     * All camera settings have been changed.
     * 
     * <p>Uses @SuppressWarnings to suppress the PMD warning, because this
     * test uses Mockito's verify method.
     */
    @Test
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    public void testUpdateAll() {
        Camera c = new Camera();
        connection = spy(new LiveCameraConnection(CAMERA_IP));
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
    
    /**
     * Tests update method:
     * Only pan and tilt values have been changed.
     * 
     * <p>Uses @SuppressWarnings to suppress the PMD warning, because this
     * test uses Mockito's verify method.
     */
    @Test
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    public void testUpdatePanTiltOnly() {
        Camera c = new Camera();
        connection = spy(new LiveCameraConnection(CAMERA_IP));
        doReturn(new CameraSettings(0, 0, 0, 0)).when(connection).getCurrentCameraSettings();
        doReturn(true).when(connection).absPanTilt(1965, 65);
        connection.update(c, new CameraSettings(1965, 65, 0, 0));
        verify(connection).absPanTilt(1965, 65);
        verify(connection, never()).absZoom(anyInt());
        verify(connection, never()).absFocus(anyInt());
        Camera.clearAllCameras();
    }
    
    /**
     * Tests update method:
     * Only the zoom value has been changed.
     * 
     * <p>Uses @SuppressWarnings to suppress the PMD warning, because this
     * test uses Mockito's verify method.
     */
    @Test
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    public void testUpdateZoomOnly() {
        Camera c = new Camera();
        connection = spy(new LiveCameraConnection(CAMERA_IP));
        doReturn(new CameraSettings(0, 0, 0, 0)).when(connection).getCurrentCameraSettings();
        doReturn(true).when(connection).absZoom(1965);
        connection.update(c, new CameraSettings(0, 0, 1965, 0));
        verify(connection, never()).absPanTilt(anyInt(), anyInt());
        verify(connection).absZoom(1965);
        verify(connection, never()).absFocus(anyInt());
        Camera.clearAllCameras();
    }
    
    /**
     * Tests update method:
     * Only the focus value has been changed.
     * 
     * <p>Uses @SuppressWarnings to suppress the PMD warning, because this
     * test uses Mockito's verify method.
     */
    @Test
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    public void testUpdateFocusOnly() {
        Camera c = new Camera();
        connection = spy(new LiveCameraConnection(CAMERA_IP));
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
