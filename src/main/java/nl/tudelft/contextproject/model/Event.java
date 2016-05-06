package main.java.nl.tudelft.contextproject.model;

import javafx.beans.property.SimpleStringProperty;

/**
 * Class that defines an event as to be displayed in the
 * digital script.
 * 
 * @author Steven Meijer
 */
public final class Event {

    private int camera;
    private int id;
    private int preset;
    private SimpleStringProperty event;
    private SimpleStringProperty shot;

    /**
     * Constructor for creating a new Event.
     * 
     * @param shot The shot identifier
     * @param camera The camera number
     * @param preset The preset number to use with the shot
     * @param event A description of the event at the current shot
     */
    public Event(int id, String shot, int camera, int preset, String event) {
        this.id = id;
        this.shot = new SimpleStringProperty(shot);
        this.camera = camera;
        this.preset = preset;
        this.event = new SimpleStringProperty(event);
    }

    /**
     * @return The id of the event.
     */
    public int getId() {
        return this.id;
    }
    
    /**
     * @param id The id to set.
     */
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     * @return The shot.
     */
    public String getShot() {
        return shot.get();
    }

    /**
     * @param shot The shot to set.
     */
    public void setShot(String shot) {
        this.shot.set(shot);
    }

    /**
     * @return The camera # to be used.
     */
    public int getCamera() {
        return this.camera;
    }

    /**
     * @param camera The camera # to set.
     */
    public void setCamera(int camera) {
        this.camera = camera;
    }

    /**
     * @return The preset # to be used.
     */
    public int getPreset() {
        return this.preset;
    }

    /**
     * @param preset The preset # to set.
     */
    public void setPreset(int preset) {
        this.preset = preset;
    }

    /**
     * @return The description of the event.
     */
    public String getEvent() {
        return event.get();
    }

    /**
     * @param event The description of the event to set.
     */
    public void setEvent(String event) {
        this.event.set(event);
    }
}
