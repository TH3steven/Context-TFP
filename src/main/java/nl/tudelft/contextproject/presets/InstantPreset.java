package main.java.nl.tudelft.contextproject.presets;

import main.java.nl.tudelft.contextproject.camera.Camera;
import main.java.nl.tudelft.contextproject.camera.CameraSettings;

/**
 * Class to represent an instant camera preset.
 * When {@link #applyTo(Camera)} is called, this sets the specified camera
 * settings instantly to the camera.
 * @since 0.2
 */
public class InstantPreset extends Preset {
    
    /**
     * Creates an InstantPreset object with to set camera settings toSet.
     * 
     * @param toSet Camera settings to set when applied.
     * @param id The identifier of this preset.
     */
    public InstantPreset(CameraSettings toSet, int id) {
        super(toSet, id);
    }
    
    /**
     * Creates an InstantPreset object with to set camera settings toSet.
     * Also a description is added.
     * @param toSet Camera settings to set when applied.
     * @param idThe identifier of this preset.
     * @param desc The description of the preset.
     */
    public InstantPreset(CameraSettings toSet, int id, String desc) {
        super(toSet, id);
        this.setDescription(desc);
    }

    /**
     * Applies the specified camera settings instantly.
     */
    @Override
    public void applyTo(Camera cam) {
        cam.setSettings(getToSet());
    }

}
