package nl.tudelft.contextproject.saveLoad;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;

/**
 * Test for the ApplicationSettings class.
 * 
 * @since 0.7
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(ApplicationSettings.class)
public class ApplicationSettingsTest {
    
    /**
     * Asserts that the specified settings are the default settings.
     * @param settings Settings to be tested.
     */
    private static void assertDefaultSettings(ApplicationSettings settings) {
        assertEquals(ApplicationSettings.DEFAULT_RESX, settings.getRenderResX());
        assertEquals(ApplicationSettings.DEFAULT_RESY, settings.getRenderResY());
        assertEquals(ApplicationSettings.DEFAULT_VLC_LOC, settings.getVlcLocation());
        assertEquals(new HashMap<Integer, String>(), settings.getAllCameraIPs());
    }

    /**
     * Tests {@link ApplicationSettings#isLoaded()}.
     */
    @Test
    public void testIsLoaded() throws Exception {
        ApplicationSettings settings = spy(ApplicationSettings.getInstance());
        File invalidFile = new File("src/test/resources/settingsLoadInvalidTest.txt");
        File validFile = new File("src/test/resources/settingsLoadTest.txt");
        whenNew(File.class).withAnyArguments().thenReturn(invalidFile, validFile);
        assertFalse(settings.load());
        assertFalse(settings.isLoaded());
        //First file has invalid settings, second file has valid settings.
        assertTrue(settings.load());
        assertTrue(settings.isLoaded());
    }
    
    /**
     * Tests {@link ApplicationSettings#reset()}.
     */
    @Test
    public void testReset() {
        ApplicationSettings settings = ApplicationSettings.getInstance();
        settings.reset();
        assertDefaultSettings(settings);
    }

    /**
     * Tests {@link ApplicationSettings#load()}.
     * Tries to load a non-existent file.
     */
    @Test
    public void testLoadNoFile() throws Exception {
        ApplicationSettings settings = spy(ApplicationSettings.getInstance());
        settings.setRenderResolution(0, 0);
        doThrow(new FileNotFoundException()).when(settings, "getScanner");
        assertFalse(settings.load());
        assertFalse(settings.isLoaded());
    }
    
    @Test
    public void testLoadFull() {
        //TODO: fail("Not yet implemented");
    }

    /**
     * Tests {@link ApplicationSettings#save()}.
     */
    @Test
    public void testSave() {
        //TODO: fail("Not yet implemented");
    }

}
