package main.java.nl.tudelft.contextproject.script;

import main.java.nl.tudelft.contextproject.camera.Camera;

import java.util.LinkedList;
import java.util.List;

/** 
 * Class to represent a timeline of shots for a single camera.
 * 
 * @author Tabe Etta Takang Kajikaw
 * @since 0.2
 */
public class Timeline {
    
    private Camera camera;
    private List<Shot> shots;

    /**
     * Creates a Timeline object with an empty list of shots.
     */
    public Timeline() {
        this.shots = new LinkedList<Shot>();
    }
    
    /**
     * Creates a Timeline object with a certain list of shots.
     *
     * @param cam is the camera this Timeline is connected with.
     * @param shot1 is the list of shots.
     */
    public Timeline(Camera cam, List<Shot> shot1) {
        this.shots = shot1;
    }
    
    /**
     * Returns the Camera object this Timeline applies to.
     * @return the Camera object this Timeline applies to.
     */
    public Camera getCamera() {
        return camera;
    }
    
    /**
     * Sets the camera this Timeline applies to.
     * @param cam Camera this Timeline applies to.
     */
    public void setCamera(Camera cam) {
        camera = cam;
    }

    /**
     * This method is used to add shots to a Timeline.
     * @param shot is the shot to be added to the Timeline.
     */
    public void addShot(Shot shot) {
        shots.add(shot);
    }

    /**
     * This method is responsible for the execution of the shots present on our script.
     * For all the shots present in our list of shots, it calls the execute method on
     * every shot. The execute method is defined in the class Shot.
     * 
     * Not really sure what this method should do though...
     */
    public void executeScript() {
        for (Shot shot1 : shots) {
            shot1.execute();
        }
    }

}

