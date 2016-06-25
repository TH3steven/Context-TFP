package nl.tudelft.contextproject.saveLoad;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.spy;

import nl.tudelft.contextproject.camera.Camera;
import nl.tudelft.contextproject.camera.CameraSettings;
import nl.tudelft.contextproject.presets.InstantPreset;
import nl.tudelft.contextproject.script.Script;
import nl.tudelft.contextproject.script.Shot;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ApplicationSettings.class)
@PowerMockIgnore("javax.crypto.*")
public class LoadScriptTest {
    
    private final String saveFileLocation = "src/test/resources/loadScriptTest.xml";
    
    /**
     * Cleans up the cameras after the test.
     */
    @After
    public void cleanUp() {
        Camera.clearAllCameras();
    }
    
    /**
     * Clears the environment for testing.
     */
    @Before
    public void setUp() {
        Camera.clearAllCameras();
        ApplicationSettings settings = spy(ApplicationSettings.getInstance());
        settings.reset();
        doNothing().when(settings).initCameraConnections();
    }

    @Test
    public void testLoad() {
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
            LoadScript.setLoadLocation(saveFileLocation);
            Script loadedScript = LoadScript.load();
            assertEquals(script, loadedScript);
        } catch (XMLStreamException e) {
            e.printStackTrace();
            fail("Some XML thing went wrong");
        }
    }
}
