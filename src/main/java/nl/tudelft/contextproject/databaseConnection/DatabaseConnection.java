package nl.tudelft.contextproject.databaseConnection;

import nl.tudelft.contextproject.camera.Camera;
import nl.tudelft.contextproject.camera.CameraSettings;
import nl.tudelft.contextproject.presets.InstantPreset;
import nl.tudelft.contextproject.presets.Preset;
import nl.tudelft.contextproject.saveLoad.ApplicationSettings;
import nl.tudelft.contextproject.script.Script;
import nl.tudelft.contextproject.script.Shot;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Class to create a database connection. 
 * The connection information is stored in the application settings.
 * 
 * @since 0.8
 */

public final class DatabaseConnection extends Observable {

    private static final String COUNTER_TABLE = "counter";
    private static final String SCRIPT_TABLE = "script";
    private static final String PRESET_TABLE = "preset";
    private static final DatabaseConnection INSTANCE = new DatabaseConnection();
    
    private int counter;
    private Connection conn = null;
    private ApplicationSettings settings; 
    private Timer timer;

    /**
     * Initializes a database connection object. 
     * Private constructor since this is a singeleton class.
     */
    private DatabaseConnection() {
        settings = ApplicationSettings.getInstance();
        counter = -1;

        try {
            Class.forName(settings.getJdbcDriver());   
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the singleton instance of this class.
     * @return the singleton instance of this class.
     */
    public static DatabaseConnection getInstance() {
        return INSTANCE;
    }

    /**
     * Creates a MySQL database connection with the properties specified in the settings.
     * @throws SQLException When no connection can be made, a SQLException will be thrown.
     */
    public void connect() throws SQLException {
        String url = "jdbc:mysql://" + settings.getDatabaseUrl() + ":" 
                + settings.getDatabasePort() + "/" + settings.getDatabaseName();
        conn = DriverManager.getConnection(url, settings.getDatabaseUsername(), settings.getDatabasePassword());
    }

    /**
     * Apply the (changed) settings to the connection.
     * @throws SQLException When the settings are invalid, an SQLException will be thrown.
     */
    public void updateSettings() throws SQLException {
        settings = ApplicationSettings.getInstance();

        if (conn != null) {
            conn.close();
        }

        connect();
    }

    /**
     * Add an observer for the current counter. 
     * This will start the timer that will update the counter every 200 ms.
     */
    @Override
    public void addObserver(Observer o) {
        if (countObservers() == 0) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        int actualCounter = getCounter();
                        
                        if (actualCounter != counter) {
                            counter = actualCounter;
                            setChanged();
                            notifyObservers(actualCounter);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }, 200);            
        }
        super.addObserver(o);
    }
    
    @Override
    public void deleteObserver(Observer o) {
        super.deleteObserver(o);
        if (countObservers() == 0) {
            timer.cancel();
        }
    }
    
    @Override
    public void deleteObservers() {
        super.deleteObservers();
        timer.cancel();
    }
    
    /**
     * Used to check whether a connection is valid.
     * 
     * @param timeout The timeout used before declaring a connection invalid.
     * @return True if the connection is valid, otherwise false.
     */
    public boolean isValid(int timeout) {
        if (conn == null) {
            return false;
        }

        try {
            return conn.isValid(timeout);
        } catch (SQLException e) {
            return false;
        } 
    }

    /**
     * If there is no valid connection, reconnect.
     * @throws SQLException When no connection can be made, this exception will be thrown.
     */
    private void revalidate() throws SQLException {
        if (conn == null || !isValid(200)) {
            connect();
        }
    }

    /**
     * Updates the counter by adding 1 to it.
     * @throws SQLException When no connection can be made, this exception will be thrown.
     */
    public void updateCounter() throws SQLException {
        revalidate();
        setCounter(getCounter() + 1);
    }

    /**
     * Sets the counter to 0.
     * @throws SQLException When no connection can be made, this exception will be thrown.
     */
    public void resetCounter() throws SQLException {
        revalidate();
        setCounter(0);    
    }

    /**
     * Set the counter in the database.
     * 
     * @param number The new value for the counter.
     * @throws SQLException When no connection can be made, this exception will be thrown.
     */
    private void setCounter(int number) throws SQLException {
        revalidate();

        Statement stmt = conn.createStatement();
        String query = "UPDATE " + COUNTER_TABLE + " set number='" + number + "'";
        stmt.executeUpdate(query);
        stmt.close();
    }

    /**
     * Returns the current value of the counter in the database.
     * 
     * @return The current value of the counter in the database.
     * @throws SQLException When no connection can be made, this exception will be thrown.
     */
    public int getCounter() throws SQLException {
        revalidate();

        Statement stmt = conn.createStatement();
        String query = "SELECT number FROM " + COUNTER_TABLE;
        ResultSet rs = stmt.executeQuery(query);
        rs.next();

        int res = rs.getInt("number");

        rs.close();
        stmt.close();

        return res;
    }

    /**
     * Clears the script in the database and uploads a new script.
     * 
     * <p>Uses @SuppressWarnings for the PMD warning about appending consecutive String
     * literals. Fixing these would compromise the readability of the code.
     * 
     * @param script The script to upload.
     * @throws SQLException When no connection can be made, this exception will be thrown.
     */
    @SuppressWarnings("PMD.ConsecutiveLiteralAppends")
    public void uploadScript(Script script) throws SQLException {
        revalidate();
        clearScript();

        Statement stmt = conn.createStatement();
        Iterator<Shot> iterator = script.getShots().iterator();

        while (iterator.hasNext()) {
            Shot shot = iterator.next();
            StringBuilder sBuilder = new StringBuilder(50);
            sBuilder.append("INSERT INTO " + SCRIPT_TABLE + " VALUES (")
                .append("'" + shot.getNumber() + "',")
                .append("'" + shot.getShotId() + "',")
                .append("'" + shot.getCamera().getNumber() + "',")
                .append("'" + shot.getPreset().getId() + "',")
                .append("'" + shot.getDescription() + "',")
                .append("'" + shot.getAction() + "');");
            stmt.executeUpdate(sBuilder.toString());
        }

        stmt.close();
    }

    /**
     * Clears the script table in the database.
     * @throws SQLException When no connection can be made, this exception will be thrown.
     */
    private void clearScript() throws SQLException {
        Statement stmt = conn.createStatement();
        String query = "DELETE FROM " + SCRIPT_TABLE;
        stmt.executeUpdate(query);
        stmt.close();
    }

    /**
     * Returns the script currently stored in the database.
     * 
     * @return The script currently stored in the database.
     * @throws SQLException When no connection can be made, this exception will be thrown.
     */
    public Script getScript() throws SQLException {
        Script script = new Script(new ArrayList<Shot>());
        revalidate();
        Statement stmt = conn.createStatement();

        String query = "SELECT * FROM " + SCRIPT_TABLE;
        ResultSet rs = stmt.executeQuery(query);

        while (rs.next()) {
            int number = rs.getInt("number");
            int cameraId = rs.getInt("camera");
            int presetId = rs.getInt("preset");

            String shotId = rs.getString("shotId");
            String description = rs.getString("description");
            String action = rs.getString("action");

            Camera cam = Camera.getCamera(cameraId);
            Preset preset = cam.getPreset(presetId);
            Shot shot = new Shot(number, shotId, cam, preset, description, action);
            script.addShot(shot);
        }

        rs.close();
        stmt.close();

        return script;
    }

    /**
     * Clears all preset currently in the Database.
     * 
     * @throws SQLException When no connection can be made, this exception will be thrown.
     */
    public void clearPresets() throws SQLException {
        revalidate();

        Statement stmt = conn.createStatement();
        String query = "DELETE FROM " + PRESET_TABLE;
        stmt.executeUpdate(query);
        stmt.close();
    }

    /**
     * Upload a preset to the database.
     * 
     * @param preset The preset to upload.
     * @param camera The camera of the preset.
     * @throws SQLException When no connection can be made, this exception will be thrown.
     */
    public void uploadPreset(Preset preset, Camera camera) throws SQLException {
        revalidate();

        Statement stmt = conn.createStatement();
        String query = "SELECT * FROM " + PRESET_TABLE + " WHERE id='" + preset.getId() 
            + "' AND camera='" + camera.getNumber() + "';";
        ResultSet rs = stmt.executeQuery(query);

        if (!rs.next()) {
            stmt.executeUpdate(insertPreset(preset, camera));
        } else {
            stmt.executeUpdate(updatePreset(preset, camera));
        }

        rs.close();
        stmt.close();
    }

    /**
     * Creates the query to insert a preset in the database.
     * 
     * <p>Uses @SuppressWarnings for the PMD warning about appending consecutive String
     * literals. Fixing these would compromise the readability of the code.
     * 
     * @param preset The preset to create the query for.
     * @param camera The camera of the preset.
     * @return A query inserting the preset in the database.
     */
    @SuppressWarnings("PMD.ConsecutiveLiteralAppends")
    private String insertPreset(Preset preset, Camera camera) {
        String type = "";

        if (preset instanceof InstantPreset) {
            type = "InstantPreset";
        }

        StringBuilder sBuilder = new StringBuilder(50);
        sBuilder.append("INSERT INTO " + PRESET_TABLE + " VALUES (")
            .append("'" + preset.getId() + "',")
            .append("'" + camera.getNumber() + "',")
            .append("'" + type + "',")
            .append("'" + preset.getDescription() + "',")
            .append("'" + preset.getImage() + "',")
            .append("'" + preset.getToSet().getPan() + "',")
            .append("'" + preset.getToSet().getTilt() + "',")
            .append("'" + preset.getToSet().getZoom() + "',")
            .append("'" + preset.getToSet().getFocus() + "');");

        return sBuilder.toString();
    }

    /**
     * Creates the query to update (overwrite) an already existing preset in the database.
     * 
     * <p>Uses @SuppressWarnings for the PMD warning about appending consecutive String
     * literals. Fixing these would compromise the readability of the code.
     * 
     * @param preset The preset to create the query for.
     * @param camera The camera of the preset.
     * @return A query updating the preset in the database.
     */
    @SuppressWarnings("PMD.ConsecutiveLiteralAppends")
    private String updatePreset(Preset preset, Camera camera) {
        String type = "";

        if (preset instanceof InstantPreset) {
            type = "InstantPreset";
        }

        StringBuilder sBuilder = new StringBuilder(130);
        sBuilder
            .append("UPDATE " + PRESET_TABLE + " SET ")
            .append("id='" + preset.getId() + "',")
            .append("camera='" + camera.getNumber() + "',")
            .append("type='" + type + "',")
            .append("description='" + preset.getDescription() + "',")
            .append("imageLocation='" + preset.getImage() + "',")
            .append("pan='" + preset.getToSet().getPan() + "',")
            .append("tilt='" + preset.getToSet().getTilt() + "',")
            .append("zoom='" + preset.getToSet().getZoom() + "',")
            .append("focus='" + preset.getToSet().getFocus() + "' ")
            .append("WHERE id='" + preset.getId() + "' AND camera='" + camera.getNumber() + "';");

        return sBuilder.toString();
    }

    /**
     * Updates the presets by getting them from the database and adding them to the cameras.
     * 
     * @param overwrite True if you want to overwrite already existing presets, otherwise false.
     * @throws SQLException When no connection can be made, this exception will be thrown.
     */
    public void updatePresets(boolean overwrite) throws SQLException {
        revalidate();
        Statement stmt = conn.createStatement();

        String query = "SELECT * FROM " + PRESET_TABLE;
        ResultSet rs = stmt.executeQuery(query);

        while (rs.next()) {
            int presetId = rs.getInt("id");
            int cameraId = rs.getInt("camera");
            int pan = rs.getInt("pan");
            int tilt = rs.getInt("tilt");
            int zoom = rs.getInt("zoom");
            int focus = rs.getInt("focus");

            String desc = rs.getString("description");
            String imageLoc = rs.getString("imageLocation");
            String type = rs.getString("type");

            if (type.equals("InstantPreset")) {
                Preset preset = new InstantPreset(new CameraSettings(pan, tilt, zoom, focus), presetId);
                preset.setDescription(desc);
                preset.setImageLocation(imageLoc);
                Camera cam = Camera.getCamera(cameraId);
                if (overwrite) {
                    cam.overwritePreset(preset);
                } else {
                    cam.addPreset(preset);
                }
            }
        }

        rs.close();
        stmt.close(); 
    }
}
