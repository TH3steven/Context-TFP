package nl.tudelft.contextproject.saveLoad;

import nl.tudelft.contextproject.camera.Camera;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

/**
 * Class to hold settings for the application.
 * Can write these settings to the settings file using {@link #save()}
 * and load them from the file using {@link #load()}
 * 
 * <p>This class has high cyclomatic complexities due to the load method.
 * We did not find any good way to get around this, without sacrificing
 * code readability.
 * 
 * @since 0.7
 */
public final class ApplicationSettings {
    
    public static final int DEFAULT_RESX = 1920;
    public static final int DEFAULT_RESY = 1080;
    public static final int DEFAULT_DB_PORT = 3306;
    public static final String DEFAULT_JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    public static final String DEFAULT_VLC_LOC = "";
    
    private static final byte[] KEY = "a0@!a650".getBytes(StandardCharsets.UTF_8);

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
     * Location of the VLC installation if this is non-default.
     */
    private String vlcLocation;
    
    /**
     * URL of the database used for synchronisation.
     */
    private String databaseUrl;
    
    /**
     * Port of used for the database connection.
     */
    private int databasePort;
    
    /**
     * Name of the database.
     */
    private String databaseName;
    
    /**
     * Username of the database.
     */
    private String databaseUsername;
    
    /**
     * Password of the database.
     */
    private String databasePassword;
    
    /**
     * Location of the JDBC driver used for the database connection.
     */
    private String jdbcDriver;
    
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
        reset();
        load();
    }
    
    /**
     * Resets the settings to their default values.
     */
    public void reset() {
        resX = DEFAULT_RESX;
        resY = DEFAULT_RESY;
        databasePort = DEFAULT_DB_PORT;
        databaseUrl = "";
        databaseUsername = "";
        databasePassword = "";
        databaseName = "";
        jdbcDriver = DEFAULT_JDBC_DRIVER;
        vlcLocation = DEFAULT_VLC_LOC;
        cameraIPs = new HashMap<Integer, String>();
        Camera.clearAllCameras();
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
     * Returns {@link #databasePort}.
     * @return {@link #databasePort}
     */
    public int getDatabasePort() {
        return databasePort;
    }
    
    /**
     * Sets {@link #databasePort}.
     * @param port See {@link #databasePort}
     */
    public void setDatabasePort(int port) {
        this.databasePort = port;
    }
    
    /**
     * Return {@link #databaseUrl}.
     * @return {@link #databaseUrl}
     */
    public String getDatabaseUrl() {
        return databaseUrl;
    }
    
    /**
     * Sets {@link #databaseUrl}.
     * @param url See {@link #databaseUrl}
     */
    public void setDatabaseUrl(String url) {
        this.databaseUrl = url;
    }

    /**
     * Return {@link #databaseUsername}.
     * @return {@link #databaseUsername}
     */
    public String getDatabaseUsername() {
        return databaseUsername;
    }
    
    /**
     * Return {@link #databasePassword}.
     * @return {@link #databasePassword}
     */
    public String getDatabasePassword() {
        return databasePassword;
    }

    /**
     * Return {@link #databaseName}.
     * @return {@link #databaseName}
     */
    public String getDatabaseName() {
        return databaseName;
    }
    
    /**
     * Sets {@link #databaseName}.
     * @param name See {@link #databaseName}
     */
    public void setDatabaseName(String name) {
        this.databaseName = name;
    }
    
    /**
     * Return {@link #jdbcDriver}.
     * @return {@link #jdbcDriver}
     */
    public String getJdbcDriver() {
        return jdbcDriver;
    }
    
    /**
     * Sets {@link #jdbcDriver}.
     * @param driver See {@link #jdbcDriver}
     */
    public void setJdbcDriver(String driver) {
        this.jdbcDriver = driver;
    }
    
    /**
     * Updates the information required for a database connection.
     * 
     * @param url The URL of the database.
     * @param port The port of the database.
     * @param name The name of the database.
     * @param username The username to access the database.
     * @param password The password to access the database.
     */
    public void setDatabaseInfo(String url, int port, String name, String username, String password) {
        this.databaseUrl = url;
        this.databasePort = port;
        this.databaseName = name;
        this.databaseUsername = username;
        this.databasePassword = password;
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
     * Clears all camera IPs.
     */
    public void clearAllCameraIPs() {
        cameraIPs.clear();
    }
    
    /**
     * Encrypts a password.
     * 
     * @param password the password to encrypt.
     * @return the encrypted password.
     */
    private String encrypt(String password) {
        try {
            final String meth = "PBEWithMD5AndDES";
            char[] spec = new char[]{'!', '6', 'j', '9', 'n', 'R', 'b', 'S', 'n', '%'};
            Cipher pbeCipher = Cipher.getInstance(meth);
            for (int i = 0; i < spec.length; i++) {
                spec[i / 2 + 3] += 2;
            }
            pbeCipher.init(Cipher.ENCRYPT_MODE, 
                    SecretKeyFactory.getInstance(meth)
                    .generateSecret(new PBEKeySpec(spec)),
                    new PBEParameterSpec(KEY, 20));
            return Base64.getEncoder().encodeToString(pbeCipher.doFinal(password.getBytes("UTF-8")));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Decrypts a password.
     * 
     * @param password password to decrypt.
     * @return the decrypted password.
     */
    private String decrypt(String password) {
        try {
            final String meth = "PBEWithMD5AndDES";
            char[] ciph = new char[]{'!', '6', 'j', '9', 'n', 'R', 'b', 'S', 'n', '%'};
            Cipher pbeCipher = Cipher.getInstance(meth);
            for (int i = 0; i < ciph.length; i++) {
                ciph[i / 2 + 3] += 2;
            }
            pbeCipher.init(Cipher.DECRYPT_MODE, 
                    SecretKeyFactory.getInstance(meth)
                    .generateSecret(new PBEKeySpec(ciph)),
                    new PBEParameterSpec(KEY, 20));
            return new String(pbeCipher.doFinal(Base64.getDecoder().decode(password)), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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
     * Loads settings from the settings file, returns
     * true was successful, returns false iff not.
     * @return true iff settings were loaded from the
     *      settings file.
     */
    public boolean load() {
        try {
            Scanner sc = getScanner();
            while (sc.hasNext()) {
                switch (sc.next()) {
                    case "resX":
                        resX = sc.hasNextInt() ? sc.nextInt() : resX;
                        break;
                    case "resY":
                        resY = sc.hasNextInt() ? sc.nextInt() : resY;
                        break;
                    case "vlcLocation":
                        vlcLocation = sc.hasNext() ? sc.nextLine().trim() : vlcLocation;
                        break;
                    case "cameraIPs":
                        loadCameraIPs(sc);
                        break;
                    case "databaseUrl":
                        databaseUrl = sc.hasNext() ? sc.nextLine().trim() : databaseUrl;
                        break;
                    case "databasePort":
                        databasePort = sc.hasNextInt() ? sc.nextInt() : databasePort;
                        break;
                    case "databaseUsername":
                        databaseUsername = sc.hasNext() ? sc.nextLine().trim() : databaseUsername;
                        break;
                    case "databasePassword":
                        databasePassword = sc.hasNext() ? decrypt(sc.nextLine().trim()) : databasePassword;
                        break;
                    case "databaseName":
                        databaseName = sc.hasNext() ? sc.nextLine().trim() : databaseName;
                        break;
                    case "jdbcDriver":
                        jdbcDriver = sc.hasNext() ? sc.nextLine().trim() : jdbcDriver;
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
            Camera cam = new Camera();
            if (sc.hasNextLine() && camId - 1 == cam.getNumber()) {
                cameraIPs.put(camId - 1, sc.nextLine().trim());
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
        for (Camera cam : Camera.getAllCameras()) {
            int key = cam.getNumber();
            String ip = cameraIPs.get(key) == null ? "" : cameraIPs.get(key);
            writer.println((key + 1) + " " + ip);
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
        writer.println("databaseUrl " + databaseUrl);
        writer.println("databasePort " + databasePort);
        writer.println("databaseName " + databaseName);
        writer.println("databaseUsername " + databaseUsername);
        writer.println("databasePassword " + encrypt(databasePassword));
        writer.println("jdbcDriver " + jdbcDriver);
    }
}
