package main.java.nl.tudelft.contextproject.camera;

import main.java.nl.tudelft.contextproject.presets.Preset;

import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;
import java.util.Observable;

/**
 * Class to represent a camera.
 * Extends Observables so its settings can be observed.
 * @since 0.2
 */
public class Camera extends Observable {
    
    private static final HashMap<Integer, Camera> CAMERAS = new HashMap<Integer, Camera>();
    private static int numCams = 0;
    
    private int num;
    private CameraSettings camSet;
    private HashMap<Integer, Preset> presets;
    private CameraConnection connection;
    
    /**
     * Creates a Camera object with initial camera settings
     * set to the lower limits of the camera.
     */
    public Camera() {
        camSet = new CameraSettings();
        num = numCams++;
        presets = new HashMap<Integer, Preset>();
        CAMERAS.put(num, this);
    }
    
    /**
     * Creates a Camera object with initial camera settings
     * as specified in the CameraSettings object.
     * 
     * @param init Initial camera settings.
     */
    public Camera(CameraSettings init) {
        camSet = init;
        num = numCams++;
        presets = new HashMap<Integer, Preset>();
        CAMERAS.put(num, this);
    }
    
    /**
     * Returns the camera with a specific number, or null if it
     * does not yet exist.
     * @param camNum number of the camera to get
     * @return the camera with the associated number, or null if
     *      it does not exist.
     */
    public static Camera getCamera(int camNum) {
        return CAMERAS.get(camNum);
    }
    
    /**
     * Returns all cameras currently made.
     * @return a collection of all cameras currently specified.
     */
    public static Collection<Camera> getAllCameras() {
        return CAMERAS.values();
    }
    
    public static void clearAllCameras() {
        CAMERAS.clear();
        numCams = 0;
    }
    
    /**
     * Gets the camera number assigned to the camera.
     * @return Camera number assigned to camera.
     */
    public int getNumber() {
        return num;
    }
    
    /**
     * Returns the camera settings.
     * @return Camera settings
     */
    public CameraSettings getSettings() {
        return camSet;
    }
    
    /**
     * Sets the settings for this camera.
     * Updates the observers.
     * 
     * @param settings Camera settings to set.
     */
    public void setSettings(CameraSettings settings) {
        camSet = settings;
        setChanged();
        notifyObservers(settings);
    }
    
    /**
     * Returns true iff the camera has a non-null CameraConnection
     * object.
     * @return true iff the camera has a non-null CameraConnection
     */
    public boolean hasConnection() {
        return connection != null;
    }
    
    /**
     * Returns the CameraConnection object used for communicating with
     * the actual camera. May be null if it has not yet been initialized.
     * @return the CameraConnection object used for communicating with
     *      the actual camera.
     */
    public CameraConnection getConnection() {
        return connection;
    }
    
    /**
     * Sets the CameraConnection object used for communicating with
     * the actual camera. It adds this new connection as an observer
     * and if there was a previous connection, removes the previous
     * connection as observer.
     * @param connect the new connection to the camera.
     */
    public void setConnection(CameraConnection connect) {
        if (hasConnection()) {
            this.deleteObserver(connection);
        }
        connection = connect;
        this.addObserver(connect);
    }
    
    /**
     * Get the total amount of cameras connected to the system.
     * @return The number of cameras.
     */
    public static int getCameraAmount() {
        return numCams;
    } 
    
    /**
     * Pans the camera a certain offset. Cannot pan past
     * the pan limits.
     * 
     * @param offset The offset to pan the camera.
     */
    public void pan(int offset) {
        camSet.pan(offset);
        if (hasConnection()) {
            connection.relPan(offset);
        }
        setChanged();
        notifyObservers();
    }
    
    /**
     * Tilts the camera a certain offset. Cannot tilt past
     * the tilt limits.
     * 
     * @param offset The offset to tilt the camera.
     */
    public void tilt(int offset) {
        camSet.tilt(offset);
        if (hasConnection()) {
            connection.relTilt(offset);
        }
        setChanged();
        notifyObservers();
    }
    
    /**
     * Zooms the camera a certain offset. Cannot zoom past
     * the zoom limits.
     * 
     * @param offset The offset to zoom the camera.
     */
    public void zoom(int offset) {
        camSet.zoom(offset);
        if (hasConnection()) {
            connection.relZoom(offset);
        }
        setChanged();
        notifyObservers();
    }

    /**
     * Zooms the camera a certain focus. Cannot focus past
     * the focus limits.
     *
     * @param offset The offset to focus the camera.
     */
    public void focus(int offset) {
        camSet.focus(offset);
        if (hasConnection()) {
            connection.relFocus(offset);
        }
        setChanged();
        notifyObservers();
    }

    public void absPanTilt(int panValue, int tiltValue) {
        camSet.setPan(panValue);
        camSet.setTilt(tiltValue);
        if (hasConnection()) {
            connection.absPanTilt(panValue, tiltValue);
        }
        setChanged();
        notifyObservers();
    }

    public void absPan(int value) {
        camSet.setPan(value);
        if (hasConnection()) {
            connection.absPan(value);
        }
        setChanged();
        notifyObservers();
    }

    public void absTilt(int value) {
        camSet.setTilt(value);
        if (hasConnection()) {
            connection.absTilt(value);
        }
        setChanged();
        notifyObservers();
    }

    public void absZoom(int value) {
        camSet.setZoom(value);
        if (hasConnection()) {
            connection.absZoom(value);
        }
        setChanged();
        notifyObservers();
    }

    public void absFocus(int value) {
        camSet.setFocus(value);
        if (hasConnection()) {
            connection.absFocus(value);
        }
        setChanged();
        notifyObservers();
    }
    /**
     * Adds a preset to the camera, if there is not already
     * a preset with the same id. Returns true if successful.
     * @param p The preset to add to this camera.
     * @return True if the preset was added, otherwise false.
     */
    public boolean addPreset(Preset p) {
        if (presets.get(p.getId()) == null) {
            presets.put(p.getId(), p);
            notifyObservers();
            return true;
        }
        return false;
    }
    
    /**
     * Adds a preset, overwriting if it already exists.
     * @param p The preset to add.
     */
    public void overwritePreset(Preset p) {
        presets.put(p.getId(), p);
        notifyObservers();
    }
    
    /**
     * Removes a preset from the camera.
     * @param p The preset to remove.
     */
    public void removePreset(Preset p) {
        presets.remove(p.getId());
    }
    
    /**
     * Returns the preset with the specified id.
     * Returns null if the preset doesn't exist.
     * @param id The id of the preset to get.
     * @return The requested preset.
     */
    public Preset getPreset(int id) {
        return presets.get(id);
    }
    
    /**
     * Returns a hashmap with all the presets of this camera.
     * @return A hashmap with all the presets of this camera.
     */
    public HashMap<Integer, Preset> getPresets() {
        return presets;
    }
    
    /**
     * Returns the amount of presets currently registered to this camera.
     * @return Amount of presets.
     */
    public int getPresetAmount() {
        return presets.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Camera)) {
            return false;
        }
        Camera camera = (Camera) o;
        boolean result = num == camera.num
                && Objects.equals(camSet, camera.camSet)
                && Objects.equals(presets, camera.presets);
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(num, camSet, presets);
    }

    /**

     * Returns the list of presets currently registered to this camera.
     * @return the list of presets registered to this camera.
     */
    public Collection<Preset> getAllPresets() {
        return presets.values();
    }
    
}
