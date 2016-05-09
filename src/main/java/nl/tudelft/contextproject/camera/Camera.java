package main.java.nl.tudelft.contextproject.camera;

import main.java.nl.tudelft.contextproject.presets.Preset;

import java.util.HashMap;
import java.util.Observable;

/**
 * Class to represent a camera.
 * Extends Observables so its settings can be observed.
 * 
 * @author Bart van Oort
 * @since 0.2
 */
public class Camera extends Observable {
    
    private static int numCams = 0;
    
    private int num;
    private CameraSettings camSet;
    private HashMap<Integer, Preset> presets;
    
    /**
     * Creates a Camera object with initial camera settings
     * set to the lower limits of the camera.
     */
    public Camera() {
        camSet = new CameraSettings();
        num = numCams++;
        presets = new HashMap<Integer, Preset>();
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
        notifyObservers();
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
     * Returns the amount of presets currently registered.
     * @return Amount of presets.
     */
    public int getPresetAmount() {
        return presets.size();
    }
    
    /**
     * This is still to be implemented but should be responsible for the taking
     * of shots by a camera.
     */
    public void takeShot() {

    }
    
}
