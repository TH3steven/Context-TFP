package nl.tudelft.contextproject.script;

import nl.tudelft.contextproject.ContextTFP;

import java.util.TimerTask;

public class UpdateTask extends TimerTask {
    private Script script = ContextTFP.getScript();
    
    /**
     * Updates the old camera, which was live during the previous shot,
     * to its next preset.
     */
    public void run() {
        if (script.getCurrent() > -1) {
            Shot old = script.getCurrentShot();
            script.getTimeline(old.getCamera().getNumber()).nextPreset(old);
        }
    }
}
