package test.java.nl.tudelft.contextproject.saveLoad;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import main.java.nl.tudelft.contextproject.camera.Camera;
import main.java.nl.tudelft.contextproject.camera.CameraSettings;
import main.java.nl.tudelft.contextproject.presets.InstantPreset;
import main.java.nl.tudelft.contextproject.saveLoad.LoadScript;
import main.java.nl.tudelft.contextproject.script.Script;
import main.java.nl.tudelft.contextproject.script.Shot;

import org.junit.Test;

public class LoadScriptTest {
    
    private final String saveFileLocation = "src/test/resources/loadScriptTest.xml";

    @Test
    public void testLoad() {
        List<Shot> shots = new ArrayList<Shot>();
        Camera cam0 = new Camera();
        Camera cam1 = new Camera();
        shots.add(new Shot(0, cam0, new InstantPreset(new CameraSettings(1, 1, 1, 2), 1)));
        shots.add(new Shot(1, cam1, new InstantPreset(new CameraSettings(2, 2, 2, 2), 2)));
        shots.add(new Shot(2, cam1, new InstantPreset(new CameraSettings(3, 3, 3, 3), 3)));
        shots.add(new Shot(3, cam0, new InstantPreset(new CameraSettings(4, 4, 4, 4), 4)));
        Script script = new Script(shots);
        
        try {
            LoadScript.setLoadLocation(saveFileLocation);
            Script loadedScript = LoadScript.load();
            assertEquals(script, loadedScript);
        } catch (XMLStreamException e) {
            e.printStackTrace();
            fail("Some XML thing went wrong");
        }
    }

}
