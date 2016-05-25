package nl.tudelft.contextproject.camera;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/*
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
*/







import org.junit.Before;
//import org.junit.BeforeClass;
import org.junit.Test;

public class LiveCameraConnectionTest {
    
    private static boolean doTests = true;
    private static final int MAX_MOV_OFFSET = 2;
    
    //private static HttpServer server;
    private LiveCameraConnection connection;
    
    
    /*          //Work in progress to create test that works offline.
    @BeforeClass
    public static void setUpClass() throws IOException {
        server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/cgi-bin", (exchange) -> {
            if (exchange.getRequestMethod().equals("GET")) {
                String data = exchange.getRequestURI().toString();
                data = data.substring(20, data.lastIndexOf("&res=1"));
                sendResponse(exchange, data);
            } else {
                fail();
            }
        });
        server.setExecutor(null);
        server.start();
    }
    
    private static void sendResponse(HttpExchange exchange, String command) throws IOException {
        switch (command) {
            case "QID":
                String response = "OID:AW-HE130";
                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
                break;
        }
    }
    */

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
        }
    }

    @Test
    public void testAbsPanTilt() throws InterruptedException {
        if (doTests) {
            assertTrue(connection.absPanTilt(29965, 29965));
            Thread.sleep(4000);
            int[] curSet = connection.getCurrentPanTilt();
            assertTrue(29965 - MAX_MOV_OFFSET <= curSet[0]);
            assertTrue(29965 + MAX_MOV_OFFSET >= curSet[0]);
            assertTrue(29965 - MAX_MOV_OFFSET <= curSet[1]);
            assertTrue(29965 + MAX_MOV_OFFSET >= curSet[1]);
        }
    }

    @Test
    public void testAbsPan() throws InterruptedException {
        if (doTests) {
            int[] before = connection.getCurrentPanTilt();
            assertTrue(connection.absPan(31965));
            Thread.sleep(4000);
            int[] after = connection.getCurrentPanTilt();
            assertTrue(before[1] - MAX_MOV_OFFSET <= after[1]);
            assertTrue(before[1] + MAX_MOV_OFFSET >= after[1]);
            assertTrue(31965 - MAX_MOV_OFFSET <= after[0]);
            assertTrue(31965 + MAX_MOV_OFFSET >= after[0]);
        }
    }

    @Test
    public void testAbsTilt() throws InterruptedException {
        if (doTests) {
            int[] before = connection.getCurrentPanTilt();
            assertTrue(connection.absTilt(31965));
            Thread.sleep(4000);
            int[] after = connection.getCurrentPanTilt();
            assertTrue(before[0] - MAX_MOV_OFFSET <= after[0]);
            assertTrue(before[0] + MAX_MOV_OFFSET >= after[0]);
            assertTrue(31965 - MAX_MOV_OFFSET <= after[1]);
            assertTrue(31965 + MAX_MOV_OFFSET >= after[1]);
        }
    }

    @Test
    public void testAbsZoom() throws InterruptedException {
        if (doTests) {
            assertTrue(connection.absZoom(1965));
            Thread.sleep(2000);
            int newZoom = connection.getCurrentZoom();
            assertTrue(1965 - MAX_MOV_OFFSET <= newZoom);
            assertTrue(1965 + MAX_MOV_OFFSET >= newZoom);
        }
    }

    @Test
    public void testAbsFocusNoAutoFocus() {
        if (doTests) {
            assertTrue(connection.setAutoFocus(false));
            assertTrue(connection.absFocus(1965));
            int newFocus = connection.getCurrentFocus();
            assertTrue(1965 - MAX_MOV_OFFSET <= newFocus);
            assertTrue(1965 + MAX_MOV_OFFSET >= newFocus);
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
            assertTrue(before[0] - 420 - MAX_MOV_OFFSET <= after[0]);
            assertTrue(before[0] - 420 + MAX_MOV_OFFSET >= after[0]);
            assertTrue(before[1] + 420 - MAX_MOV_OFFSET <= after[1]);
            assertTrue(before[1] + 420 + MAX_MOV_OFFSET >= after[1]);
        }
    }

    @Test
    public void testRelPan() throws InterruptedException {
        if (doTests) {
            int[] before = connection.getCurrentPanTilt();
            assertTrue(connection.relPan(420));
            Thread.sleep(2000);
            int[] after = connection.getCurrentPanTilt();
            assertTrue(before[0] + 420 - MAX_MOV_OFFSET <= after[0]);
            assertTrue(before[0] + 420 + MAX_MOV_OFFSET >= after[0]);
            assertTrue(before[1] - MAX_MOV_OFFSET <= after[1]);
            assertTrue(before[1] + MAX_MOV_OFFSET >= after[1]);
        }
    }

    @Test
    public void testRelTilt() throws InterruptedException {
        if (doTests) {
            int[] before = connection.getCurrentPanTilt();
            assertTrue(connection.relTilt(420));
            Thread.sleep(2000);
            int[] after = connection.getCurrentPanTilt();
            assertTrue(before[0] - MAX_MOV_OFFSET <= after[0]);
            assertTrue(before[0] + MAX_MOV_OFFSET >= after[0]);
            assertTrue(before[1] + 420 - MAX_MOV_OFFSET <= after[1]);
            assertTrue(before[1] + 420 + MAX_MOV_OFFSET >= after[1]);
        }
    }

    @Test
    public void testRelZoom() throws InterruptedException {
        if (doTests) {
            int oldZoom = connection.getCurrentZoom();
            assertTrue(connection.relZoom(65));
            Thread.sleep(1000);
            int newZoom = connection.getCurrentZoom();
            assertTrue(oldZoom + 65 - MAX_MOV_OFFSET <= newZoom);
            assertTrue(oldZoom + 65 + MAX_MOV_OFFSET >= newZoom);
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
            assertTrue(oldFocus + 65 - MAX_MOV_OFFSET <= newFocus);
            assertTrue(oldFocus + 65 + MAX_MOV_OFFSET >= newFocus);
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
    public void testUpdate() {
        if (doTests) {
            fail("Not yet implemented");
        }
    }

}
