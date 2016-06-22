package nl.tudelft.contextproject.databaseConnection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import nl.tudelft.contextproject.camera.Camera;
import nl.tudelft.contextproject.camera.CameraSettings;
import nl.tudelft.contextproject.presets.InstantPreset;
import nl.tudelft.contextproject.presets.Preset;
import nl.tudelft.contextproject.saveLoad.ApplicationSettings;
import nl.tudelft.contextproject.script.Script;
import nl.tudelft.contextproject.script.Shot;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Observable;

public class DatabaseConnectionTest {
    
    private static DatabaseConnection connection;
    private int counter;

    /**
     * Set up the settings for the database.
     * @throws SQLException Throws an exception when no connection can be made.
     */
    @BeforeClass
    public static void setUp() throws SQLException {
        char[] pass = "k^^pfp_^^s".toCharArray();
        for (int i = 1; i < pass.length - 1; i++) {
            pass[i] += 3;
        }
        ApplicationSettings.getInstance().setDatabaseInfo("159.253.0.125", 3306, 
                "thomaaj84_test", "thomaaj84_test", new String(pass));
        ApplicationSettings.getInstance().setDatabaseName("thomaaj84_test");
        connection = DatabaseConnection.getInstance();
    }
    
    /**
     * Resets the settings when all the tests in this class are finished.
     */
    @AfterClass
    public static void cleanUp() {
        ApplicationSettings.getInstance().reset();       
    }
    
    /**
     * Resets the cameras after each test.
     */
    @After
    public void after() {
        Camera.clearAllCameras();
    }
    
    /**
     * Tests the isValid method.
     */
    @Test
    public void testIsValid() {
        assertTrue(connection.isValid(200));
    }
    
    /**
     * Tests all methods regarding the counter.
     * @throws SQLException Throws an exception when no connection can be made.
     */
    @Test
    public void testCounter() throws SQLException {
        connection.resetCounter();
        connection.updateCounter();
        connection.updateCounter();
        assertEquals(2, connection.getCounter());
    }
    
    /**
     * Tests all methods regarding the script.
     * @throws SQLException Throws an exception when no connection can be made.
     */
    @Test
    public void testScript() throws SQLException {
        Camera cam0 = new Camera();
        Camera cam1 = new Camera();
        Preset pres = new InstantPreset(new CameraSettings(1, 1, 1, 2), 1, "desccc");
        Preset pres2 = new InstantPreset(new CameraSettings(1, 3, 2, 5), 2, "dsdoifj");
        Preset pres3 = new InstantPreset(new CameraSettings(2, 4, 5, 3), 3, "sdfioj");
        Shot shot1 = new Shot(1, "1a", cam0, pres, "desc", "action");
        Shot shot2 = new Shot(2, "22", cam1, pres2, "des2c", "action2");
        Shot shot3 = new Shot(3, "new 3", cam0, pres3, "desc333", "ac564on");
        ArrayList<Shot> los = new ArrayList<Shot>();
        los.add(shot1);
        los.add(shot2);
        los.add(shot3);
        Script script = new Script(los);
        connection.uploadScript(script);
        assertEquals(script, connection.getScript());
    }
    
    /**
     * Tests the uploading of presets.
     * @throws SQLException Throws an exception when no connection can be made.
     */
    @Test
    public void testPreset() throws SQLException {
        Camera cam0 = new Camera();
        Camera cam1 = new Camera();
        Preset pres = new InstantPreset(new CameraSettings(1, 1, 1, 2), 1, "desccc");
        Preset pres2 = new InstantPreset(new CameraSettings(1, 3, 2, 5), 2, "dsdoifj");
        Preset pres3 = new InstantPreset(new CameraSettings(2, 4, 5, 3), 3, "sdfioj");
        cam0.addPreset(pres);
        cam0.addPreset(pres3);
        cam1.addPreset(pres2);
        connection.clearPresets();
        
        Collection<Preset> expected0 = cam0.getAllPresets();
        Collection<Preset> expected1 = cam1.getAllPresets();
        Iterator<Preset> it = cam0.getAllPresets().iterator();
        while (it.hasNext()) {
            connection.uploadPreset(it.next(), cam0);
        }
        connection.uploadPreset(pres2, cam1);
        cam0.getAllPresets().clear();
        cam1.getAllPresets().clear();
        connection.updatePresets(false);
        assertEquals(expected0, cam0.getAllPresets());
        assertEquals(expected1, cam1.getAllPresets());
    }
    
    /**
     * Tests if presets are succesfully overwritten.
     * @throws SQLException Throws an exception when no connection can be made.
     */
    @Test
    public void testPresetOverwrite() throws SQLException {
        Camera cam0 = new Camera();
        Camera cam1 = new Camera();
        Preset pres = new InstantPreset(new CameraSettings(1, 1, 1, 2), 1, "desccc");
        Preset pres2 = new InstantPreset(new CameraSettings(1, 3, 2, 5), 2, "dsdoifj");
        Preset pres3 = new InstantPreset(new CameraSettings(2, 4, 5, 3), 3, "sdfioj");
        cam0.addPreset(pres);
        cam0.addPreset(pres3);
        cam1.addPreset(pres2);
        connection.clearPresets();
        
        Iterator<Preset> it = cam0.getAllPresets().iterator();
        //Upload current presets
        while (it.hasNext()) {
            connection.uploadPreset(it.next(), cam0);
        }
        connection.uploadPreset(pres2, cam1);

        //Upload new preset
        Preset presOverwrite = new InstantPreset(new CameraSettings(1, 3, 5, 3), 3, "sjhloj");
        connection.uploadPreset(presOverwrite, cam0);
        connection.updatePresets(true);
        assertTrue(cam0.getAllPresets().contains(presOverwrite));
    }
    
    /**
     * Tests if the implemention of the observable class is correct.
     * 
     * @throws SQLException Throws an exception when no connection can be made.
     * @throws InterruptedException Thrown when the thread cannot be stopped.
     */
    @Test 
    public void observerTest() throws SQLException, InterruptedException {
        connection.resetCounter();
        counter = -1;
        connection.addObserver( (Observable obj, Object arg) -> {
            counter = (int) arg;
        });
        
        connection.updateCounter();
        connection.updateCounter();
        Thread.sleep(800);
        assertEquals(2, counter);
        connection.deleteObservers();
    }
}
