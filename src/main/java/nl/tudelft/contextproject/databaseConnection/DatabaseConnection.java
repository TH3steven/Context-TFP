package nl.tudelft.contextproject.databaseConnection;

import nl.tudelft.contextproject.saveLoad.ApplicationSettings;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
     * Updates the counter by adding 1 to it.
     * @throws SQLException When no connection can be made, this exception will be thrown.
     */
    public void updateCounter() throws SQLException {
        setCounter(getCounter() + 1);
    }
    
    /**
     * Sets the counter to 0.
     * @throws SQLException When no connection can be made, this exception will be thrown.
     */
    public void resetCounter() throws SQLException {
        setCounter(0);    
    }
    
    /**
     * Set the counter in the database.
     * 
     * @param number The new value for the counter.
     * @throws SQLException When no connection can be made, this exception will be thrown.
     */
    private void setCounter(int number) throws SQLException {
        if (conn == null) {
            throw new SQLException();
        }
        
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
        if (conn == null) {
            throw new SQLException();
        }
        
        Statement stmt = conn.createStatement();
        String query = "SELECT number FROM " + COUNTER_TABLE;
        ResultSet rs = stmt.executeQuery(query);
        int id = rs.getInt("number");
        rs.close();
        stmt.close();
        return id;
    }
}
