package main.java.nl.tudelft.contextproject.model;

import javafx.beans.property.SimpleStringProperty;

/**
 * Class that defines an event as to be displayed in the
 * digital script.
 * 
 * @author Steven Meijer
 */
public final class Event {
    private SimpleStringProperty shot;
    private SimpleStringProperty camera;
    private SimpleStringProperty preset;
    private SimpleStringProperty event;
    
    public Event(String shot, String camera, String preset, String event) {
        this.shot = new SimpleStringProperty(shot);
        this.camera = new SimpleStringProperty(camera);
        this.preset = new SimpleStringProperty(preset);
        this.event = new SimpleStringProperty(event);
    }

    /**
     * 
     * @return The shot.
     */
    public String getShot() {
        return shot.get();
    }

    /**
     * 
     * @param shot The shot to set.
     */
    public void setShot(String shot) {
        this.shot.set(shot);
    }

    /**
     * 
     * @return The camera # to be used.
     */
    public String getCamera() {
        return camera.get();
    }

    /**
     * 
     * @param camera The camera # to set.
     */
    public void setCamera(String camera) {
        this.camera.set(camera);
    }

    /**
     * 
     * @return The preset # to be used.
     */
    public String getPreset() {
        return preset.get();
    }

    /**
     * 
     * @param preset The preset # to set.
     */
    public void setPreset(String preset) {
        this.preset.set(preset);
    }

    /**
     * 
     * @return The description of the event.
     */
    public String getEvent() {
        return event.get();
    }

    /**
     * 
     * @param event The description of the event to set.
     */
    public void setEvent(String event) {
        this.event.set(event);
    }
}
