package nl.tudelft.contextproject.databaseConnection;

import nl.tudelft.contextproject.saveLoad.ApplicationSettings;

import org.junit.BeforeClass;

public class DatabaseConnectionTest {

    /**
     * Set up the settings for the database.
     */
    @BeforeClass
    public void setUp() {
        ApplicationSettings settings = ApplicationSettings.getInstance();
        //TODO: Add real password.
        settings.setDatabaseInfo("46.21.172.161", 3306, "pmacjhdy_test", "password");
    }
}
