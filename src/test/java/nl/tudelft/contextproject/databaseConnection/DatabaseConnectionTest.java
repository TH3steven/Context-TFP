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

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.SQLException;
import java.util.ArrayList;

public class DatabaseConnectionTest {
    
    private static DatabaseConnection connection;

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
        ApplicationSettings.getInstance().setDatabaseInfo("46.21.172.161", 3306, "pmacjhdy_test", new String(pass));
        ApplicationSettings.getInstance().setDatabaseName("pmacjhdy_test");
        connection = DatabaseConnection.getInstance();
    }
    
    /**
     * Resets the settings and clears all cameras.
     */
    @AfterClass
    public static void cleanUp() {
        ApplicationSettings.getInstance().reset();
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
}
