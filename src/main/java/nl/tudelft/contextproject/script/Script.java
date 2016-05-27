package nl.tudelft.contextproject.script;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Class to represent a script of presets.
 * Implements the Iterator interface so it can apply
 * presets as the list of presets is being traversed.
 * 
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
     * The name of the script as displayed on the ui.
     */
    private String name;
    
    /**
     * Creates a script that starts from the beginning with specified shots.
     * Current is initialized with -1, so the first call of next() returns the first shot.
     * @param shots The actual script of the different shots in order of appearance.
     */
    public Script(List<Shot> shots) {
        this.shots = shots;
        current = -1;
        name = "";
        timelines = new HashMap<Integer, Timeline>();
        initTimelines();
        initPresetLoading();
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
     * Loads the first presets of all the cameras.
     */
    private void initPresetLoading() {
        Set<Integer> keys = timelines.keySet();
        for (Integer i : keys) {
            timelines.get(i).initPreset();
        }
    }
    
    /**
     * Adds a shot to the Script, also adds it to the timelines.
     * If the shot is associated to a camera that does not have
     * a timeline associated with it in the script, it will create
     * a new timeline for it.
     * 
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
    
    /**
     * Returns the current shot, null if there is no such shot.
     * @return Current shot.
     */
    public Shot getCurrentShot() {
        try {
            return shots.get(current);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Returns the shot after the current shot, null if there is no such shot.
     * @return Shot after the current shot.
     */
    public Shot getNextShot() {
        if (hasNext()) {
            return shots.get(current + 1);
        }
        return null;
    }
    
    /**
     * Returns the name of the script. The name is set when a script is saved.
     * @return The name of the script.
     */
    public String getName() {
        return name;
    }
    
    /**
     * Sets the name of the script.
     * @param name The name of the script.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns true if there is a next shot, the +1 is used because we initialize with -1.
     */
    @Override
    public boolean hasNext() {
        return current + 1 < shots.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Script)) {
            return false;
        }
        Script script = (Script) o;
        return Objects.equals(getShots(), script.getShots());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getShots());
    }

    /**
     * Does what {@link Iterator#next} does, but also
     * executes the shot ({@link Shot#execute()} while doing so. 
     * The method also loads the next preset of the camera that was live.
     */
    @Override
    public Shot next() {
        if (current > -1) {
            Shot old = shots.get(current);
            timelines.get(old.getCamera().getNumber()).nextPreset(old);
        }
        
        current++;
        Shot next = shots.get(current);
        next.execute();
        return next;

    }
}
