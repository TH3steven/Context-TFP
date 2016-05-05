package main.java.nl.tudelft.contextproject.presets;

import main.java.nl.tudelft.contextproject.camera.Camera;
import main.java.nl.tudelft.contextproject.camera.CameraSettings;

/**
 * Class to represent a camera preset.
 * Extend this class to create different presets, with different
 * functionalities, such as application over time.
 * 
 * @author Bart van Oort
 * @since 0.2
 */
public abstract class Preset {

    private String name;
    private Camera camera;
    private CameraSettings toSet;
    
    /**
     * Creates a Preset object with an empty name with 
     * camera cam and to set camera settings toSet.
     * 
     * @param cam Camera
     * @param toSet Camera settings to set when applied.
     */
    protected Preset(Camera cam, CameraSettings toSet) {
        name = "";
        camera = cam;
        this.toSet = toSet;
    }
    
    /**
     * Returns the camera the preset should be applied to.
     * "MOOMM!! GET THE CAMERAAA!!"
     * 
     * @return the camera the preset is to be applied to.
     */
    public Camera getCamera() {
        return camera;
    }
    
    /**
     * Returns the settings the camera should be set to.
     * @return To be applied camera settings
     */
    public CameraSettings getToSet() {
        return toSet;
    }
    
    /**
     * Applies the preset to the camera.
     */
    public abstract void apply();
}
