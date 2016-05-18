package nl.tudelft.contextproject.presets;

import nl.tudelft.contextproject.camera.Camera;
import nl.tudelft.contextproject.camera.CameraSettings;

import java.util.Objects;

/**
 * Class to represent a camera preset.
 * Extend this class to create different presets, with different
 * functionalities, such as application over time.
 * 
 * {@link #applyTo(Camera)} should be implemented to apply the
 * preset to the camera, in its respective way.
 * @since 0.2
 */
public abstract class Preset {

    private String description;
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
        this.description = "";
        this.toSet = toSet;
        this.id = identifier;
        imageLocation = "";
    }
    
    /**
     * Returns the identifier of this preset.
     * @return The identifier of this preset.
     */
    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Preset)) {
            return false;
        }
        Preset preset = (Preset) o;
        boolean result = getId() == preset.getId()
                && Objects.equals(getDescription(), preset.getDescription())
                && Objects.equals(getToSet(), preset.getToSet())
                && Objects.equals(imageLocation, preset.imageLocation);
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDescription(), getToSet(), getId(), imageLocation);
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
        this.imageLocation = loc;
    }
    
    /**
     * Returns the location of the preview image.
     * @return Location of the preview image.
     */
    public String getImage() {
        return this.imageLocation;
    }
    
    /**
     * Sets the description of the preset.
     * @param desc The description of the preset.
     */
    public void setDescription(String desc) {
        this.description = desc;
    }
    
    /**
     * Returns the description of the preset.
     * @return The description of the preset.
     */
    public String getDescription() {
        return this.description;
    }
    
    /**
     * Applies the preset to the camera.
     * @param cam Camera the preset should be applied to.
     */
    public abstract void applyTo(Camera cam);
}
