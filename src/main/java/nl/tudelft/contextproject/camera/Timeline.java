package main.java.nl.tudelft.contextproject.camera;


import main.java.nl.tudelft.contextproject.presets.Preset;
import main.java.nl.tudelft.contextproject.shot.Shot;
import java.util.*;

import java.util.ArrayList;
import java.util.List;

/** Class to represent the execution of commands,
 * concerning all the shots in our script.
 * Created by Team Free Pizza on 03/05/16.
 */
public class Timeline {
    private List<Shot> shots = new ArrayList<Shot>();

    /**
     * creates a timeline object with a list of shots
     * present in our script.
     *
     * @param shot1 is the list to which the shots have to be added.
     */
    public Timeline(List<Shot> shot1) {
        this.shots = shot1;
    }

    /**
     * This method is used to add shots to our list of shots.
     * @param shot is the shot to be added to the list of present shots.
     */
    public void addShot(Shot shot) {
        shots.add(shot);
    }

    /**
     * This method is responsible for the execution of the shots present on our script.
     * For all the shots present in our list of shots, it calls the execute method on
     * every shot. The execute method is defined in the class Shot.
     */
    public void executeScript() {
        for ( Shot shot1 : shots) {
            shot1.execute();
        }
    }

}

