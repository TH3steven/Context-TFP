package main.java.nl.tudelft.contextproject.script;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Class to represent a script of presets.
 * Implements the Iterator interface so it can apply
 * presets as the list of presets is being traversed.
 * 
 * @author Bart van Oort
 * @since 0.2
 */
public class Script implements Iterator<Shot> {
    
    /**
     * Contains the Timelines per camera number.
     */
    private HashMap<Integer, Timeline> timelines;
    
    /**
     * Contains the actual script of shots, like the director defined
     * it on paper.
     */
    private List<Shot> shots;
    
    /**
     * Keeps track of the current shot.
     */
    private int current;
    
    /**
     * Creates a script that starts from the beginning
     * with specified shots.
     * @param shots The actual script of the different shots in order of appearance.
     */
    public Script(List<Shot> shots) {
        this.shots = shots;
        current = 0;
        timelines = new HashMap<Integer, Timeline>();
        initTimelines();
    }

    /**
     * Gets all the shots in our script.
     * @return the shots present in our scripts.
     */
    public List<Shot> getShots() {
        return shots;
    }

    /**
     * Returns the timeline for a specific Camera.
     * @param camNum is the camera number of the timeline to be returned.
     * @return the timeline for the camera number camNum.
     */
    public Timeline getTimeline(int camNum) {
        return timelines.get(camNum);
    }

    /**
     * Checks if the list of shots is empty.
     * @return true if the list of shots is actually empty.
     */
    public boolean isEmpty() {
        return shots.size() == 0;
    }

    /**
     * Initializes the hashmap containing the timelines per camera.
     */
    private void initTimelines() {
        for (Shot s : shots) {
            if (timelines.containsKey(s.getCamera().getNumber())) {
                timelines.get(s.getCamera().getNumber()).addShot(s);
            } else {
                Timeline t = new Timeline();
                t.setCamera(s.getCamera());
                t.addShot(s);
                timelines.put(s.getCamera().getNumber(), t);
            }
        }
    }
    
    /**
     * Adds a shot to the Script, also adds it to the timelines.
     * If the shot is associated to a camera that does not have
     * a timeline associated with it in the script, it will create
     * a new timeline for it.
     * @param s shot to be added.
     */
    public void addShot(Shot s) {
        shots.add(s);
        if (timelines.containsKey(s.getCamera().getNumber())) {
            timelines.get(s.getCamera().getNumber()).addShot(s);
        } else {
            Timeline t = new Timeline(s.getCamera(), new LinkedList<Shot>());
            t.addShot(s);
            timelines.put(s.getCamera().getNumber(), t);
        }
    }

    @Override
    public boolean hasNext() {
        return current < shots.size();
    }

    /**
     * Does what {@link Iterator#next} does, but also
     * executes the shot ({@link Shot#execute()} while doing so.
     */
    @Override
    public Shot next() {
        Shot s = shots.get(current);
        s.execute();
        current++;
        return s;
    }
}
