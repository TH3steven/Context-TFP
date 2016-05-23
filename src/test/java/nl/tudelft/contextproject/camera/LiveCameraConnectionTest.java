package nl.tudelft.contextproject.camera;

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
        }
    }

    @Test
    public void testAbsPanTilt() throws InterruptedException {
        if (doTests) {
            assertTrue(connection.absPanTilt(19965, 19965));
            Thread.sleep(4000);
            CameraSettings curSet = connection.getCurrentCameraSettings();
            assertTrue(19965 - MAX_MOV_OFFSET <= curSet.getPan());
            assertTrue(19965 + MAX_MOV_OFFSET >= curSet.getPan());
            assertTrue(19965 - MAX_MOV_OFFSET <= curSet.getTilt());
            assertTrue(19965 + MAX_MOV_OFFSET >= curSet.getTilt());
        }
    }

    @Test
    public void testAbsPan() throws InterruptedException {
        if (doTests) {
            CameraSettings before = connection.getCurrentCameraSettings();
            assertTrue(connection.absPan(31965));
            Thread.sleep(4000);
            CameraSettings after = connection.getCurrentCameraSettings();
            assertTrue(before.getTilt() - MAX_MOV_OFFSET <= after.getTilt());
            assertTrue(before.getTilt() + MAX_MOV_OFFSET >= after.getTilt());
            assertTrue(31965 - MAX_MOV_OFFSET <= after.getPan());
            assertTrue(31965 + MAX_MOV_OFFSET >= after.getPan());
        }
    }

    @Test
    public void testAbsTilt() {
        if (doTests) {
            fail("Not yet implemented");
        }
    }

    @Test
    public void testAbsZoom() {
        if (doTests) {
            fail("Not yet implemented");
        }
    }

    @Test
    public void testAbsFocus() {
        if (doTests) {
            fail("Not yet implemented");
        }
    }

    @Test
    public void testRelPanTilt() {
        if (doTests) {
            fail("Not yet implemented");
        }
    }

    @Test
    public void testRelPan() {
        if (doTests) {
            fail("Not yet implemented");
        }
    }

    @Test
    public void testRelTilt() {
        if (doTests) {
            fail("Not yet implemented");
        }
    }

    @Test
    public void testRelZoom() {
        if (doTests) {
            fail("Not yet implemented");
        }
    }

    @Test
    public void testRelFocus() {
        if (doTests) {
            fail("Not yet implemented");
        }
    }

    @Test
    public void testHasAutoFocus() {
        if (doTests) {
            fail("Not yet implemented");
        }
    }

    @Test
    public void testUpdate() {
        if (doTests) {
            fail("Not yet implemented");
        }
    }

}
