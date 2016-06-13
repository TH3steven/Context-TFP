package nl.tudelft.contextproject.saveLoad;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Class to hold settings for the application.
 * Can write these settings to the settings file using {@link #save()}
 * and load them from the file using {@link #load()}
 * 
 * @since 0.7
 */
public final class ApplicationSettings {
    
    public static final int DEFAULT_RESX = 1920;
    public static final int DEFAULT_RESY = 1080;
    public static final int DEFAULT_DB_PORT = 3306;
    public static final String DEFAULT_JDBC_DRIVER = "com.mysql.jdbc.Driver";
    public static final String DEFAULT_VLC_LOC = "";

    private static final ApplicationSettings INSTANCE = new ApplicationSettings();
    private static final String PATH = "settings.tfp";
    
    /**
     * Resolution in X direction with which VLC will render.
     */
    private int resX;
    
    /**
     * Resolution in Y direction with which VLC will render.
     */
    private int resY;
    
    /**
     * Port of used for the database connection.
     */
    private int database_Port;
    
    /**
     * Location of the VLC installation if this is non-default.
     */
    private String vlcLocation;
    
    /**
     * URL of the database used for synchronization.
     */
    private String database_Url;
    
    /**
     * Username of the database.
     */
    private String database_Username;
    
    /**
     * Password of the database.
     */
    private String database_Password;
    
    /**
     * Location of the JDBC driver used for the database connection.
     */
    private String JDBC_Driver;
    
    /**
     * Map that maps camera IDs to known camera IPs.
     */
    private HashMap<Integer, String> cameraIPs;
    
    /**
     * Constructs a new ApplicationSettings object. Tries to load from 
     * the settings file first, but if this is not present, it will load
     * default settings.
     * Private constructor since this is a singleton class.
     */
    private ApplicationSettings() {
        if (!load()) {
            reset();
        }
    }
    
    /**
     * Returns the singleton instance of this class.
     * @return the singleton instance of this class.
     */
    public static ApplicationSettings getInstance() {
        return INSTANCE;
    }
    
    /**
     * Returns {@link #resX}.
     * @return {@link #resX}
     */
    public int getRenderResX() {
        return resX;
    }
    
    /**
     * Returns {@link #resY}.
     * @return {@link #resY}
     */
    public int getRenderResY() {
        return resY;
    }
    
    /**
     * Returns {@link #database_Port}.
     * @return {@link #database_Port}
     */
    public int getDatabasePort() {
        return database_Port;
    }
    
    /**
     * Return {@link #database_Url}.
     * @return {@link #database_Url}
     */
    public String getDatabaseUrl() {
        return database_Url;
    }

    /**
     * Return {@link #database_Username}.
     * @return {@link #database_Username}
     */
    public String getDatabaseUsername() {
        return database_Username;
    }
    
    /**
     * Return {@link #database_Password}.
     * @return {@link #database_Password}
     */
    public String getDatabasePassword() {
        return database_Password;
    }
    
    /**
     * Return {@link #JDBC_Driver}.
     * @return {@link #JDBC_Driver}
     */
    public String getJDBCDriver() {
        return JDBC_Driver;
    }
    
    /**
     * Updates the information required for a database connection.
     * 
     * @param url The url of the database.
     * @param port The port of the database.
     * @param username The username to access the database.
     * @param password The password to access the database.
     */
    public void updateDatabase(String url, int port, String username, String password) {
        this.database_Url = url;
        this.database_Port = port;
        this.database_Username = username;
        this.database_Password = password;
    }
    
    public void setJDBCDriver(String driver) {
        this.JDBC_Driver = driver;
    }
    
    /**
     * Sets the render resolution setting with which VLC will will render 
     * live camera views to the specified settings.
     * 
     * @param resX Resolution in X direction
     * @param resY Resolution in Y direction
     */
    public void setRenderResolution(int resX, int resY) {
        this.resX = resX;
        this.resY = resY;
    }
    
    /**
     * Returns {@link #vlcLocation}.
     * @return {@link #vlcLocation}
     */
    public String getVlcLocation() {
        return vlcLocation;
    }
    
    /**
     * Sets {@link #vlcLocation}.
     * @param loc New {@link #vlcLocation}
     */
    public void setVlcLocation(String loc) {
        vlcLocation = loc;
    }
    
    /**
     * Returns the IP of the camera with camera id camId
     * or null if unknown.
     * @return the IP of the camera with camera id camId
     *      or null if unknown.
     */
    public String getCameraIP(int camId) {
        return cameraIPs.get(camId);
    }
    
    /**
     * Returns a HashMap containing all currently specified IPs for
     * a given camera number.
     * @return a HashMap that maps a camera number to an IP address.
     */
    protected HashMap<Integer, String> getAllCameraIPs() {
        return cameraIPs;
    }
    
    /**
     * Adds a camera IP to a camera number.
     * @param camId Id of camera
     * @param ip IP of the camera with id camId.
     */
    public void addCameraIP(int camId, String ip) {
        cameraIPs.put(camId, ip);
    }
    
    /**
     * Constructs a Scanner object to read the settings file.
     * @return a Scanner object with the settings file as input.
     * @throws FileNotFoundException When the file could not be found
     */
    private Scanner getScanner() throws FileNotFoundException {
        return new Scanner(new File(PATH));
    }
    
    /**
     * Constructs a PrintWriter object to write to the settings file.
     * @return a PrintWriter object with the settings file as output.
     * @throws IOException When the file cannot be created or cannot be
     *      opened for some reason.
     */
    private PrintWriter getWriter() throws IOException {
        return new PrintWriter(new BufferedWriter(new FileWriter(PATH, false)));
    }
    
    /**
     * Returns true iff the current settings are valid, that is iff:
     * <blockquote>{@link #resX} != 0
     *      <br>{@link #resY} != 0
     *      <br>{@link #vlcLocation} != null
     *      <br>{@link #cameraIPs} != null
     * </blockquote>
     * @return true iff the current settings are valid.
     */
    public boolean isLoaded() {
        return resX != 0 && resY != 0 && vlcLocation != null && cameraIPs != null;
    }
    
    /**
     * Resets the settings their default values.
     */
    public void reset() {
        resX = DEFAULT_RESX;
        resY = DEFAULT_RESY;
        database_Port = DEFAULT_DB_PORT;
        database_Url = "";
        database_Username = "";
        database_Password = "";
        JDBC_Driver = DEFAULT_JDBC_DRIVER;
        vlcLocation = DEFAULT_VLC_LOC;
        cameraIPs = new HashMap<Integer, String>();
    }
    
    /**
     * Loads settings from the settings file, returns
     * true was successful, returns false iff not.
     * @return true iff settings were loaded from the
     *      settings file.
     */
    public boolean load() {
        try {
            Scanner sc = getScanner();
            vlcLocation = DEFAULT_VLC_LOC;
            cameraIPs = new HashMap<Integer, String>();
            while (sc.hasNext()) {
                switch (sc.next()) {
                    case "resX":
                        resX = sc.hasNextInt() ? sc.nextInt() : DEFAULT_RESX;
                        break;
                    case "resY":
                        resY = sc.hasNextInt() ? sc.nextInt() : DEFAULT_RESY;
                        break;
                    case "vlcLocation":
                        vlcLocation = sc.hasNext() ? sc.nextLine().trim() : vlcLocation;
                        break;
                    case "cameraIPs":
                        loadCameraIPs(sc);
                        break;
                    case "database_Url":
                        database_Url = sc.hasNext() ? sc.nextLine().trim() : database_Url;
                        break;
                    case "database_Port":
                        database_Port = sc.hasNextInt() ? sc.nextInt() : DEFAULT_DB_PORT;
                        break;
                    case "database_Username":
                        database_Username = sc.hasNext() ? sc.nextLine().trim() : database_Username;
                        break;
                    case "JDBC_Driver":
                        JDBC_Driver = sc.hasNext() ? sc.nextLine().trim() : DEFAULT_JDBC_DRIVER;
                        break;
                    default:
                        break;
                }
            }
            sc.close();
            return isLoaded();
        } catch (FileNotFoundException e) {
            return false;
        }
    }
    
    /**
     * Loads the camera IP addresses in the cameraIPs section
     * of the settings file.
     * @param sc Scanner at the position after reading the
     *      cameraIPs section header.
     */
    private void loadCameraIPs(Scanner sc) {
        while (sc.hasNextInt()) {
            int camId = sc.nextInt();
            if (sc.hasNext()) {
                cameraIPs.put(camId, sc.next());
            }
        }
    }
    
    /**
     * Saves the settings to the settings file.
     * @throws IOException in the case that the name of
     *      the settings file is a directory rather than
     *      a file, or cannot be opened for some other
     *      reason.
     */
    public void save() throws IOException {
        PrintWriter writer = getWriter();
        writer.println("resX " + resX);
        writer.println("resY " + resY);
        if (!vlcLocation.equals("")) {
            writer.println("vlcLocation " + vlcLocation);
        }
        writer.println("cameraIPs");
        for (int key : cameraIPs.keySet()) {
            writer.println(key + " " + cameraIPs.get(key));
        }
        saveDatabaseInformation(writer);
        writer.flush();
        writer.close();
    }
    
    /**
     * Saves the database information to a file using a writer.
     * @param writer The writer that should be used for saving.
     */
    private void saveDatabaseInformation(PrintWriter writer) {
        writer.println("database_Url " + database_Url);
        writer.println("database_Port " + database_Port);
        writer.println("database_Username " + database_Username);
        writer.println("JDBC_Driver " + JDBC_Driver);
    }
}
