package nl.tudelft.contextproject.saveLoad;

import static org.junit.Assert.fail;

import nl.tudelft.contextproject.camera.Camera;
import nl.tudelft.contextproject.camera.CameraSettings;
import nl.tudelft.contextproject.presets.InstantPreset;
import nl.tudelft.contextproject.script.Script;
import nl.tudelft.contextproject.script.Shot;

import org.junit.After;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;

public class SaveScriptTest {
    
    private final String saveFileLocation = "src/test/resources/saveScriptTest.xml";
    
    @After
    public void cleanUp() {
        Camera.clearAllCameras();
    }

    @Test
    public void testSave() {
        List<Shot> shots = new ArrayList<Shot>();
        Camera cam0 = new Camera();
        Camera cam1 = new Camera();
        shots.add(new Shot(0, cam0, new InstantPreset(new CameraSettings(1, 1, 1, 1), 1)));
        shots.add(new Shot(1, cam1, new InstantPreset(new CameraSettings(2, 2, 2, 2), 2)));
        shots.add(new Shot(2, cam1, new InstantPreset(new CameraSettings(3, 3, 3, 3), 3)));
        shots.add(new Shot(3, cam0, new InstantPreset(new CameraSettings(4, 4, 4, 4), 4)));
        shots.add(new Shot(4, cam0, null));
        Script script = new Script(shots);
        
        try {
            SaveScript.setSaveLocation(saveFileLocation);
            SaveScript.save(script);
        } catch (XMLStreamException e) {
            e.printStackTrace();
            fail("Some XML thing went wrong");
        }
    }
}
