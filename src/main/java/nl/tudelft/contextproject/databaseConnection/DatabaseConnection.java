package nl.tudelft.contextproject.databaseConnection;

import nl.tudelft.contextproject.saveLoad.ApplicationSettings;
import nl.tudelft.contextproject.script.Script;
import nl.tudelft.contextproject.script.Shot;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

public class DatabaseConnection {
    
    private static final String COUNTER_TABLE = "counter";
    private static final String SCRIPT_TABLE = "script";
    private static final String PRESET_TABLE = "preset";

    private Connection conn = null;
    private ApplicationSettings settings; 
    
    /**
     * Initializes a database connection object.
     */
    public DatabaseConnection() {
        settings = ApplicationSettings.getInstance();
        
        try {
            Class.forName(settings.getJdbcDriver());   
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
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
        conn.close();
        connect();
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
            if (conn.isValid(timeout)) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            return false;
        } 
    }
    
    /**
     * If there is no valid connection, reconnect.
     * @throws SQLException When no connection can be made, this exception will be thrown.
     */
    private void checkValid() throws SQLException {
        if (conn == null || !isValid(100)) {
            connect();
        }
    }
    
    /**
     * Updates the counter by adding 1 to it.
     * @throws SQLException When no connection can be made, this exception will be thrown.
     */
    public void updateCounter() throws SQLException {
        checkValid();
        setCounter(getCounter() + 1);
    }
    
    /**
     * Sets the counter to 0.
     * @throws SQLException When no connection can be made, this exception will be thrown.
     */
    public void resetCounter() throws SQLException {
        checkValid();
        setCounter(0);    
    }
    
    /**
     * Set the counter in the database.
     * 
     * @param number The new value for the counter.
     * @throws SQLException When no connection can be made, this exception will be thrown.
     */
    private void setCounter(int number) throws SQLException {
        checkValid();
        
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
        checkValid();
        
        Statement stmt = conn.createStatement();
        String query = "SELECT number FROM " + COUNTER_TABLE;
        ResultSet rs = stmt.executeQuery(query);
        int id = rs.getInt("number");
        rs.close();
        stmt.close();
        return id;
    }
    
    /**
     * Clears the script in the database and uploads a new script.
     * 
     * @param script The script to upload.
     * @throws SQLException When no connection can be made, this exception will be thrown.
     */
    public void uploadScript(Script script) throws SQLException {
        checkValid();
        clearScript();
        
        Statement stmt = conn.createStatement();
        Iterator<Shot> iterator = script.getShots().iterator();
        while (iterator.hasNext()) {
            Shot shot = iterator.next();
            StringBuilder sBuilder = new StringBuilder("INSERT INTO " + SCRIPT_TABLE + " VALUES " + "(");
            sBuilder.append("'" + shot.getNumber() + "',");
            sBuilder.append("'" + shot.getShotId() + "',");
            sBuilder.append("'" + shot.getCamera() + "',");
            sBuilder.append("'" + shot.getPreset() + "',");
            sBuilder.append("'" + shot.getDescription() + "',");
            sBuilder.append("'" + shot.getAction() + "');");
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
}
