package main.java.nl.tudelft.contextproject.presets;

import java.io.File;

import main.java.nl.tudelft.contextproject.camera.Camera;
import main.java.nl.tudelft.contextproject.camera.CameraSettings;

/**
 * Class to represent a camera preset.
 * Extend this class to create different presets, with different
 * functionalities, such as application over time.
 * 
 * {@link #applyTo(Camera)} should be implemented to apply the
 * preset to the camera, in its respective way.
 * 
 * @author Bart van Oort
 * @since 0.2
 */
public abstract class Preset {

    private String name;
    private CameraSettings toSet;
    private int id;
    private String imageLocation;
    
    /**
     * Creates a Preset object with to set camera settings toSet.
     * 
     * @param toSet Camera settings to set when applied.
     * @param identifier The identifier of this preset.
     */
    protected Preset(CameraSettings toSet, int identifier) {
        this.name = "";
        this.toSet = toSet;
        this.id = identifier;
    }
    
    /**
     * Returns the identifier of this preset.
     * @return The identifier of this preset.
     */
    public int getId() {
        return id;
    }
    
    /**
     * Returns the settings the camera should be set to.
     * @return To be applied camera settings
     */
    public CameraSettings getToSet() {
        return toSet;
    }
    
    /**
     * Sets the location of the preview image of the preset.
     * @param loc Location of the preview image.
     */
    public void setImageLocation(String loc) {
        imageLocation = loc;
    }
    
    /**
     * Returns the location of the preview image.
     * @return Location of the preview image.
     */
    public String getImage() {
        return imageLocation;
    }
    
    /**
     * Applies the preset to the camera.
     * @param cam Camera the preset should be applied to.
     */
    public abstract void applyTo(Camera cam);
}
