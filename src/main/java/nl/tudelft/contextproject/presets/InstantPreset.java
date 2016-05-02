package main.java.nl.tudelft.contextproject.presets;

import main.java.nl.tudelft.contextproject.camera.Camera;
import main.java.nl.tudelft.contextproject.camera.CameraSettings;

/**
 * Class to represent an instant camera preset.
 * When {@link #apply()} is called, this sets the specified camera
 * settings instantly to the camera.
 * 
 * @author Bart van Oort
 * @since 0.2
 */
public class InstantPreset extends Preset {
    
    /**
     * Creates an InstantPreset object with an empty name with 
     * camera cam and to set camera settings toSet.
     * 
     * @param cam Camera
     * @param toSet Camera settings to set when applied.
     */
    public InstantPreset(Camera cam, CameraSettings toSet) {
        super(cam, toSet);
    }

    /**
     * Applies the specified camera settings instantly.
     */
    @Override
    public void apply() {
       getCamera().setSettings(getToSet());
    }

}
