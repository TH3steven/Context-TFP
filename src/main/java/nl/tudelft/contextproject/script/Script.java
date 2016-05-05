package main.java.nl.tudelft.contextproject.script;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import main.java.nl.tudelft.contextproject.presets.Preset;

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
     * @param shots The actual script of the different shots in 
     * order of appearance.
     */
    public Script(List<Shot> shots) {
        this.shots = shots;
        current = 0;
        timelines = new HashMap<Integer, Timeline>();
        initTimelines();
    }
    
    private void initTimelines() {
        for (Shot s : shots) {
            if (timelines.containsKey(s.getCamera().getNumber())) {
                timelines.get(s.getCamera().getNumber()).addShot(s);
            }
            else {
                Timeline t = new Timeline();
                t.setCamera(s.getCamera());
                t.addShot(s);
                timelines.put(s.getCamera().getNumber(), t);
            }
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
