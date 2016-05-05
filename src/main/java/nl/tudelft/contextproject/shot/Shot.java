package main.java.nl.tudelft.contextproject.shot;

import main.java.nl.tudelft.contextproject.camera.Camera;
import main.java.nl.tudelft.contextproject.presets.Preset;

/**Class to represent a shot to be taken by a Camera.
 * Extend this class to analyze and the functionalities
 * or mechanism surrounding the taking of a shot,
 * and handling of shots over time.
 *
 * Created by Team Free Pizza on 03/05/16.
 */
public class Shot {
    private int number;
    private Camera camera;
    private Preset preset;


    /**
     * Creates a shot instance with each shot having a number,
     * camera and preset number.
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
     *
     * @return the number of the shot taken or to be taken.
     */
    public int getNumber() {

        return number;
    }

    /**
     *
     * @param number Sets the number of the to be taken shot.
     */
    public void setNumber(int number) {

        this.number = number;
    }
    /**
     *
     * @return the camera used to take the shot.
     */
    public Camera getCamera() {

        return camera;
    }

    /**
     *
     * @param camera sets the camera to be used to take a shot.
     */
    public void setCamera(Camera camera) {

        this.camera = camera;
    }

    /**
     *
     * @return the preset assigned to a particular shot
     */
    public Preset getPreset() {

        return preset;
    }

    /**
     *
     * @param preset sets the preset needed to take a shot.
     */
    public void setPreset(Preset preset) {

        this.preset = preset;
    }

    /**
     * This method is used to tell a particular camera to take a shot.
     * method takeshot() is defined in class Camera
     */
    public void execute() {
        this.camera.takeShot();
    }
}
