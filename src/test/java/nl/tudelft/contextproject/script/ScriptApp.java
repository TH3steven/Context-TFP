package test.java.nl.tudelft.contextproject.script;

import main.java.nl.tudelft.contextproject.camera.Camera;
import main.java.nl.tudelft.contextproject.camera.CameraSettings;
import main.java.nl.tudelft.contextproject.presets.InstantPreset;
import main.java.nl.tudelft.contextproject.script.Script;
import main.java.nl.tudelft.contextproject.script.Shot;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Demo application for how a script works.
 * @author Bart van Oort
 *
 */
public final class ScriptApp {
    
    private ScriptApp() {
        throw new UnsupportedOperationException();
    }

    /**
     * Demo application for how a script works.
     * Run and press enter to run through the script.
     * @param args Application arguments. Not used.
     */
    public static void main(String[] args) {
        List<Shot> shots = new ArrayList<Shot>();
        Camera cam0 = new Camera();
        Camera cam1 = new Camera();
        shots.add(new Shot(0, cam0, new InstantPreset(new CameraSettings(1, 1, 1, 2))));
        shots.add(new Shot(1, cam1, new InstantPreset(new CameraSettings(2, 2, 2, 2))));
        shots.add(new Shot(2, cam1, new InstantPreset(new CameraSettings(3, 3, 3, 3))));
        shots.add(new Shot(3, cam0, new InstantPreset(new CameraSettings(4, 4, 4, 4))));
        Script script = new Script(shots);
        
        Scanner sc = new Scanner(System.in);
        while (script.hasNext()) {
            sc.nextLine();
            Shot s = script.next();
            CameraSettings cs = s.getCamera().getSettings();
            System.out.println("Camera " + s.getCamera().getNumber() + " set to preset: " + cs.getPan() 
                    + ", " + cs.getTilt() + ", " + cs.getZoom());
        }
        sc.close();
    }

}
