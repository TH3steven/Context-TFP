package nl.tudelft.contextproject.script;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

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
     * Creates a script that starts from the beginning
     * with specified shots.
     * @param shots The actual script of the different shots in order of appearance.
     */
    public Script(List<Shot> shots) {
        this.shots = shots;
        current = 0;
        name = "";
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
     * Valid means that one camera doesn't have two adjacent shots with different presets.
     * @return True if a script is valid, otherwise false.
     */
    public boolean isValid() {
        if (shots.size() <= 1) {
            return true;
        }

        Shot prev = shots.get(0);
        for (int i = 1; i < shots.size(); i++) {
            Shot next = shots.get(i);
            if (next.getCamera().equals(prev.getCamera()) && !next.getPreset().equals(prev.getPreset())) {
                return false;
            }
            prev = next;
        }
        
        return true;
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
     * @return Cureent shot.
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

    @Override
    public boolean hasNext() {
        return current < shots.size();
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
     */
    @Override
    public Shot next() {
        Shot s = shots.get(current);
        s.execute();
        current++;
        return s;

    }
}
