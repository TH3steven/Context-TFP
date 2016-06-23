package nl.tudelft.contextproject.script;

import nl.tudelft.contextproject.camera.Camera;
import nl.tudelft.contextproject.camera.CameraSettings;
import nl.tudelft.contextproject.presets.InstantPreset;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Class to represent a Script of {@link Shot Shots}.
 * Implements the {@link Iterator} interface so it can apply
 * presets as the list of presets is being traversed.
 * 
 * @since 0.2
 */
public class Script implements Iterator<Shot> {
    
    public static final Shot DUMMY = new Shot(-1, "-1", Camera.DUMMY, 
            new InstantPreset(new CameraSettings(), -1), "No shot", "No action");

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
     * The timer to delay moving of a camera.
     */
    private Timer timer;

    /**
     * Creates a script that starts from the beginning with specified shots.
     * Current is initialized with -1, so the first call of next() returns the first shot.
     * 
     * @param shots The actual script of the different shots in order of appearance.
     */
    public Script(List<Shot> shots) {
        this.shots = shots;
        current = -1;
        name = "";
        timer = new Timer();
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
    public void initPresetLoading() {
        for (Timeline t : timelines.values()) {
            t.initPreset();
        }
    }
    
    /**
     * Loads the next preset for each camera depending 
     * on the current script position.
     */
    public void loadNextPresets() {
        for (Timeline t : timelines.values()) {
            t.instantNextPreset();
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
     * @return The index of the current shot in the script.
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
            return DUMMY;
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
     * Calls the updateOldCam() method after a short delay.
     * This is to give the post-production some extra footage to work with.
     */
    public synchronized void updateOldCamCaller(Shot old) {
        timer.cancel();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!old.equals(DUMMY)) {
                    Camera cam = old.getCamera();
                    timelines.get(cam.getNumber()).nextPreset(old);
                }
            }
        }, 1000);
    }
    
    /**
     * Moves all cameras to their preset, except the one
     * that is live.
     */
    public void adjustAllCameras() {
        Set<Integer> cameras = timelines.keySet();
        int liveCamera = shots.get(current).getCamera().getNumber();
        
        Shot shot;
        
        for (int i = current + 1; i < shots.size(); i++) {
            shot = shots.get(i);
            int camNum = shot.getCamera().getNumber();
            if (cameras.contains(camNum) && camNum != liveCamera) {
                shot.execute();
                cameras.remove(camNum);
            }
            if (cameras.size() == 1) {
                break;
            }
        }
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
        return next(true);
    }
    
    /**
     * Go to the next shot.
     * Depending on boolean skip, cameras are adjusted or not.
     * 
     * @param load Determines whether cameras should be adjusted.
     * @return The next shot
     */
    public Shot next(boolean load) {
        if (load) {
            updateOldCamCaller(getCurrentShot());
        } 
        
        current++;
        Shot next = shots.get(current);
        
        return next;
    }
}
