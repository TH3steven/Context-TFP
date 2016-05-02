package main.java.nl.tudelft.contextproject.camera;

import java.util.Observable;

/**
 * Class to represent a camera.
 * Extends Observables so its settings can be observed.
 * 
 * @author Bart van Oort
 * @since 0.2
 */
public class Camera extends Observable {
    
    private CameraSettings camSet;
    
    /**
     * Creates a Camera object with initial camera settings
     * set to the lower limits of the camera.
     */
    public Camera() {
        camSet = new CameraSettings();
    }
    
    /**
     * Creates a Camera object with initial camera settings
     * as specified in the CameraSettings object.
     * 
     * @param init Initial camera settings.
     */
    public Camera(CameraSettings init) {
        camSet = init;
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


    
}
