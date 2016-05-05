package main.java.nl.tudelft.contextproject.presets;

import main.java.nl.tudelft.contextproject.camera.Camera;
import main.java.nl.tudelft.contextproject.camera.CameraSettings;

/**
 * Class to represent an instant camera preset.
 * When {@link #applyTo(Camera)} is called, this sets the specified camera
 * settings instantly to the camera.
 * 
 * @author Bart van Oort
 * @since 0.2
 */
public class InstantPreset extends Preset {
    
    /**
     * Creates an InstantPreset object with to set camera settings toSet.
     * 
     * @param cam Camera
     * @param toSet Camera settings to set when applied.
     */
    public InstantPreset(CameraSettings toSet) {
        super(toSet);
    }

    /**
     * Applies the specified camera settings instantly.
     */
    @Override
    public void applyTo(Camera cam) {
        cam.setSettings(getToSet());
    }

}
