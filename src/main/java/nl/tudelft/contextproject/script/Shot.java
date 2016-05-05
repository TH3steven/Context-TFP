package main.java.nl.tudelft.contextproject.script;

import main.java.nl.tudelft.contextproject.camera.Camera;
import main.java.nl.tudelft.contextproject.presets.Preset;

/**
 * Class to represent a shot to be taken by a Camera.
 *
 * @author Tabe Etta Takang Kajikaw
 * @since 0.2
 */
public class Shot {
    private int number;
    private Camera camera;
    private Preset preset;


    /**
     * Creates a shot instance with each shot having a number,
     * camera and preset.
     * @param num is the shot number
     * @param cam is the camera used to make the shot.
     * @param pres is the preset used for the shot.
     */
    public Shot(int num, Camera cam, Preset pres) {
        this.number = num;
        this.camera = cam;
        this.preset = pres;
    }

    /**
     * Returns the number of the shot.
     * @return the number of the shot.
     */
    public int getNumber() {
        return number;
    }

    /**
     * Sets the number of the to be taken shot.
     * @param number the number of the to be taken shot.
     */
    public void setNumber(int number) {
        this.number = number;
    }
    
    /**
     * Returns the camera used to take the shot.
     * @return the camera used to take the shot.
     */
    public Camera getCamera() {
        return camera;
    }

    /**
     * Sets the camera used to take the shot.
     * @param camera the camera used to take the shot.
     */
    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    /**
     * Returns the preset assigned to this shot.
     * @return the preset assigned to this shot.
     */
    public Preset getPreset() {
        return preset;
    }

    /**
     * Sets the preset assigned to this shot.
     * @param preset the preset assigned to this shot.
     */
    public void setPreset(Preset preset) {
        this.preset = preset;
    }

    /**
     * This method is used to tell a particular camera to take a shot.
     * Makes use of {@link Camera#takeShot()}
     */
    public void execute() {
        preset.applyTo(camera);
    }
}
