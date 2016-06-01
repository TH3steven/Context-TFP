package nl.tudelft.contextproject.script;

import nl.tudelft.contextproject.camera.Camera;
import nl.tudelft.contextproject.presets.Preset;

import java.util.Objects;

/**
 * Class to represent a shot to be taken by a Camera.
 * @since 0.2
 */
public class Shot {

    private int number;
    private Camera camera;
    private Preset preset;
    private String shotId;
    private String description;
    private double duration;

    /**
     * Creates a shot instance with each shot having a number,
     * camera and preset.
     * @param num is the shot number
     * @param shotId The identifier for the shot.
     * @param cam is the camera used to make the shot.
     * @param pres is the preset used for the shot.
     * @param description The description of what happens during the shot.
     */
    public Shot(int num, String shotId, Camera cam, Preset pres, String description) {
        this.number = num;
        this.shotId = shotId;
        this.camera = cam;
        this.preset = pres;
        this.description = description;
        if (cam != null && pres != null) {
            cam.addPreset(pres);
        }
        this.duration = -1;
    }
    
    /**
     * Creates a shot instance with each shot having a number,
     * camera and no preset.
     * 
     * @param num is the shot number
     * @param shotId The identifier for the shot.
     * @param cam is the camera used to make the shot.
     * @param description The description of what happens during the shot.
     */
    public Shot(int num, String shotId, Camera cam, String description) {
        this.number = num;
        this.shotId = shotId;
        this.camera = cam;
        this.description = description;
        this.duration = -1;
    }

    /**
     * Creates a shot instance with each shot having a number,
     * camera and preset.
     * @param num is the shot number
     * @param cam is the camera used to make the shot.
     * @param pres is the preset used for the shot.
     */
    public Shot(int num, Camera cam, Preset pres) {
        this.number = num;
        this.shotId = "";
        this.camera = cam;
        this.preset = pres;
        this.description = "";
        if (cam != null && pres != null) {
            cam.addPreset(pres);
        }
        duration = -1;
    }

    /**
     * Used to tell a particular camera to take a shot.
     * Makes use of {@link Preset#applyTo(Camera)}
     */
    public void execute() {
        preset.applyTo(camera);
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
     * Returns the shot identifier of this shot.
     * @return The shotId of this shot.
     */
    public String getShotId() {
        return shotId;
    }

    /**
     * Sets the shotId of this shot.
     * @param shotId The shotId to set.
     */
    public void setShotId(String shotId) {
        this.shotId = shotId;
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
     * Returns the description of this shot.
     * @return The description of this shot.
     */
    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Shot)) {
            return false;
        }
        Shot shot = (Shot) o;
        boolean result = getNumber() == shot.getNumber()
                && Objects.equals(getCamera(), shot.getCamera())
                && Objects.equals(getPreset(), shot.getPreset())
                && Objects.equals(getShotId(), shot.getShotId())
                && Objects.equals(getDescription(), shot.getDescription());
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNumber(), getCamera(), getPreset(), getShotId(), getDescription());
    }

    /**
     * Sets the description of this shot.
     * @param description The description of this shot.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the duration of this shot in seconds.
     * @return the duration of this shot in seconds..
     */
    public double getDuration() {
        return duration;
    }

    /**
     * Sets the duration of this shot in seconds.
     * @param dur The duration of the shot in seconds.
     */
    public void setDuration(double dur) {
        duration = dur;
    }

    /**
     * Gives the shot and its number as a String.
     * @return the particular shot and its number as a string.
     */
    public String toString() {
        return "Shot " + number;
    }
}
