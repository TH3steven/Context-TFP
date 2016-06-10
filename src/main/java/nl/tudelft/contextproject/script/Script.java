package nl.tudelft.contextproject.script;

import nl.tudelft.contextproject.camera.Camera;
import nl.tudelft.contextproject.camera.CameraSettings;
import nl.tudelft.contextproject.presets.InstantPreset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Class to represent a Script of {@link Shot Shots}.
 * Implements the {@link Iterator} interface so it can apply
 * presets as the list of presets is being traversed.
 * 
 * @since 0.2
 */
public class Script extends Observable implements Iterator<Shot> {

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
     * A list of skipped shots yet to come.
     */
    private List<Shot> skippedShots;

    /**
     * Keeps track of the current shot.
     */
    private int current;

    /**
     * The name of the script as displayed on the ui.
     */
    private String name;
    
    /**
     * The timer to delay moving of a camera.
     */
    private Timer timer;
    
    /**
     * 
     */
    private boolean lastAlterationSkipped;

    /**
     * Creates a script that starts from the beginning with specified shots.
     * Current is initialized with -1, so the first call of next() returns the first shot.
     * 
     * @param shots The actual script of the different shots in order of appearance.
     */
    public Script(List<Shot> shots) {
        this.shots = shots;
        skippedShots = new ArrayList<Shot>();
        current = -1;
        name = "";
        timer = new Timer();
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
     * Gets all the shots of which the preset was not
     * automatically loaded and still yet to come.
     * 
     * @return The skipped shots.
     */
    public List<Shot> getSkippedShots() {
        return skippedShots;
    }
    
    /**
     * Add a shot to the skipped shots list and notify observers. 
     * 
     * @param shot The shot to be added.
     */
    public void addSkippedShot(Shot shot) {
        skippedShots.add(shot);
        lastAlterationSkipped = true;
        setChanged();
        notifyObservers(shot);
    }
    
    /**
     * Remove a shot from the skipped shots list and notify observers.
     * 
     * @param shot The shot to be added.
     */
    public void removeSkippedShot(Shot shot) {
        skippedShots.remove(shot);
        lastAlterationSkipped = true;
        setChanged();
        notifyObservers();
    }

    /**
     * Valid means that one camera doesn't have two adjacent shots with different presets.
     * @return The first shot to cause an error.
     */
    public Shot isValid() {
        if (shots.size() <= 1) {
            return null;
        }

        Shot prev = shots.get(0);
        for (int i = 1; i < shots.size(); i++) {
            Shot next = shots.get(i);

            if (next.getCamera().equals(prev.getCamera()) && !next.getPreset().equals(prev.getPreset())) {
                return next;
            }
            prev = next;
        }

        return null;
    }

    /**
     * Returns the timeline for a specific Camera.
     * 
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
        for (Timeline t : timelines.values()) {
            t.initPreset();
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
     * Returns the "current" variable of this class.
     * @return current
     */
    public int getCurrent() {
        return current;
    }

    /**
     * Returns the current shot, null if there is no such shot.
     * @return Current shot.
     */
    public Shot getCurrentShot() {
        try {
            return shots.get(current);
        } catch (Exception e) {
            return new Shot(-1, "-1", Camera.DUMMY, new InstantPreset(new CameraSettings(), -1), "No shot");
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
     * Returns the lastAlterationSkipped boolean.
     * Shows if the last alteration to skippedShots was an addition or removal.
     * 
     * @return The  boolean lastAlterationSkipped.
     */
    public boolean getLastAlteration() {
        return lastAlterationSkipped;
    }

    /**
     * Sets the name of the script.
     * @param name The name of the script.
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Calls the updateOldCam() method after a short delay.
     * This is to give the post-production some extra footage to work with.
     */
    public synchronized void updateOldCamCaller() {
        timer.cancel();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (current > 0) {
                    Shot old = shots.get(current - 1);
                    timelines.get(old.getCamera().getNumber()).nextPreset(old);
                }
            }
        }, 1000);
    }
    
    public void applyShot(int i) {
        skippedShots.get(i).execute();
        skippedShots.remove(i);
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
        if (o instanceof Script) {
            Script script = (Script) o;
            return Objects.equals(getShots(), script.getShots());
        }

        return false;  
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
        current++;
        Shot next = shots.get(current);
        next.execute();
        
        updateOldCamCaller();

        return next;
    }
    
    /**
     * Go to the next shot.
     * Depending on boolean skip, cameras are adjusted or not.
     * Keeps track of which shots are skipped.
     * 
     * @param skip Determines whether cameras should be adjusted.
     * @return The next shot
     */
    public Shot next(boolean skip) {
        current++;
        Shot next = shots.get(current);
        Shot old = null;
        
        if (!skip) {
            updateOldCamCaller();

            next.execute();
        } else {
            skippedShots.add(next);
            Shot skipped = null;
            
            if (current > 0) {
                old = shots.get(current - 1);
                skipped = timelines.get(old.getCamera().getNumber()).getNextShot(old);
            } 
            
            if (skipped != null) {
                addSkippedShot(skipped);
                System.out.println(skippedShots);
            }
        }
        removeSkippedShot(next);
        
        return next;
    }
}
