package nl.tudelft.contextproject.script;

import nl.tudelft.contextproject.camera.Camera;
import nl.tudelft.contextproject.presets.Preset;

import java.util.Objects;

/**
 * Class to represent a shot to be taken by a {@link Camera}. A shot
 * consists of an unique ID, a separate identifier, a camera that is
 * assigned to a shot, an optional preset that is assigned to that camera,
 * and a description of what should happen during the shot.
 * 
 * @since 0.2
 */
public class Shot {

    private Camera camera;
    private Preset preset;
    private String shotId;
    private String subject;
    private String action;

    private double duration;
    private int number;

    /**
     * Creates a shot instance with each shot having a number,
     * camera and preset.
     * 
     * @param num is the shot number
     * @param shotId The identifier for the shot.
     * @param cam is the camera used to make the shot.
     * @param pres is the preset used for the shot.
     * @param subject The subject of what happens during the shot.
     * @param action The action associated to the shot.
     */
    public Shot(int num, String shotId, Camera cam, Preset pres, String subject, String action) {
        this.number = num;
        this.shotId = shotId;
        this.camera = cam;
        this.preset = pres;
        this.subject = subject;
        if (cam != null && pres != null) {
            cam.addPreset(pres);
        }
        this.action = action;
        this.duration = -1;
    }

    /**
     * Creates a shot instance with each shot having a number,
     * camera and no preset.
     * 
     * @param num is the shot number
     * @param shotId The identifier for the shot.
     * @param cam is the camera used to make the shot.
     * @param subject The subject of what happens during the shot.
     * @param action The action associated to the shot.
     */
    public Shot(int num, String shotId, Camera cam, String subject, String action) {
        this.number = num;
        this.shotId = shotId;
        this.camera = cam;
        this.subject = subject;
        this.action = action;
        this.duration = -1;
    }

    /**
     * Creates a shot instance with each shot having a number,
     * camera and preset.
     * 
     * @param num is the shot number
     * @param cam is the camera used to make the shot.
     * @param pres is the preset used for the shot.
     */
    public Shot(int num, Camera cam, Preset pres) {
        this.number = num;
        this.shotId = "";
        this.camera = cam;
        this.preset = pres;
        this.subject = "";
        this.duration = -1;
        this.action = "";
        if (cam != null && pres != null) {
            cam.addPreset(pres);
        }
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
    
    public boolean hasPreset() {
        return (preset == null) ? false : true;
    }

    /**
     * Returns the subject of this shot.
     * @return The subject of this shot.
     */
    public String getDescription() {
        return subject;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Shot) {
            Shot shot = (Shot) o;

            return getNumber() == shot.getNumber()
                    && Objects.equals(getCamera(), shot.getCamera())
                    && Objects.equals(getPreset(), shot.getPreset())
                    && Objects.equals(getShotId(), shot.getShotId())
                    && Objects.equals(getDescription(), shot.getDescription())
                    && Objects.equals(getAction(), shot.getAction());
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNumber(), getCamera(), getPreset(), getShotId(), getDescription(), getAction());
    }

    /**
     * Sets the subject of this shot.
     * @param subject The subject of this shot.
     */
    public void setDescription(String subject) {
        this.subject = subject;
    }

    /**
     * Gets the duration of this shot in seconds.
     * @return the duration of this shot in seconds..
     */
    public double getDuration() {
        return duration;
    }

    /**
     * Gets the action to be performed by this shot.
     * @return the action to be performed by the shot
     */
    public String getAction() {
        return action;
    }

    /**
     * Sets the action to be performed by this shot.
     * @param action The precise action performed by the shot.
     */
    public void setAction(String action) {
        this.action = action;
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
    @Override
    public String toString() {
        return "Shot " + number;
    }
}
