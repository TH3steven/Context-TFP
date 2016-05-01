package main.java.nl.tudelft.contextproject.camera;

/**
 * Class to represent a camera preset.
 * 
 * @author Bart van Oort
 * @since 0.2
 */
public class Preset {
    
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
    public Preset(Camera cam, CameraSettings toSet) {
        name = "";
        camera = cam;
        this.toSet = toSet;
    }
    
    /**
     * Applies the preset to the camera.
     */
    public void apply() {
        camera.setSettings(toSet);
    }
}
