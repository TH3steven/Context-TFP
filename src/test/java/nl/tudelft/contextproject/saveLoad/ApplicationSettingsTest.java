package nl.tudelft.contextproject.saveLoad;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.whenNew;
import nl.tudelft.contextproject.camera.Camera;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Test for the ApplicationSettings class.
 * 
 * <p>Uses @SuppressWarnings to suppress the PMD warning, because using JUnit and
 * PowerMock(ito) brings a lot of static imports, but they do not decrease
 * the readability of the code. 
 * @since 0.7
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(ApplicationSettings.class)
@SuppressWarnings("PMD.TooManyStaticImports")
@PowerMockIgnore("javax.crypto.*")
public class ApplicationSettingsTest {
    
    /**
     * Asserts that the specified settings are the default settings.
     * @param settings Settings to be tested.
     */
    private static void assertDefaultSettings(ApplicationSettings settings) {
        assertEquals(ApplicationSettings.DEFAULT_RESX, settings.getRenderResX());
        assertEquals(ApplicationSettings.DEFAULT_RESY, settings.getRenderResY());
        assertEquals(ApplicationSettings.DEFAULT_VLC_LOC, settings.getVlcLocation());
        assertEquals(ApplicationSettings.DEFAULT_DB_PORT, settings.getDatabasePort());
        assertEquals(ApplicationSettings.DEFAULT_JDBC_DRIVER, settings.getJdbcDriver());
        assertEquals(new HashMap<Integer, String>(), settings.getAllCameraIPs());
    }

    /**
     * Tests {@link ApplicationSettings#isLoaded()}.
     * Loads two different files.
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
    
    /**
     * Tests {@link ApplicationSettings#load()}. Loads a full settings file.
     * 
     * <p>Uses @SuppressWarnings to suppress the warning about a hardcoded IP,
     * which isn't meant to be an actual IP.
     */
    @Test
    @SuppressWarnings("PMD.AvoidUsingHardCodedIP")
    public void testLoadFull() throws Exception {
        ApplicationSettings settings = spy(ApplicationSettings.getInstance());
        settings.reset();
        File file = new File("src/test/resources/settingsLoadTest.txt");
        whenNew(File.class).withAnyArguments().thenReturn(file);
        settings.load();
        HashMap<Integer, String> expectedIPs = new HashMap<Integer, String>();
        expectedIPs.put(0, "65.65.65.65");
        expectedIPs.put(1, "420.420.420.420");
        assertEquals(1965, settings.getRenderResX());
        assertEquals(420, settings.getRenderResY());
        assertEquals("D:\\program files", settings.getVlcLocation());
        assertEquals(expectedIPs, settings.getAllCameraIPs());
        assertEquals("google.nl", settings.getDatabaseUrl());
        assertEquals(1996, settings.getDatabasePort());
        assertEquals("henk", settings.getDatabaseUsername());
        assertEquals("ingrid", settings.getDatabaseName());
        assertEquals("com.mysql.jdbc.Test", settings.getJdbcDriver());
    }

    /**
     * Tests {@link ApplicationSettings#save()}.
     * 
     * <p>Uses @SuppressWarnings to suppress the warning about a hardcoded IP,
     * which isn't meant to be an actual IP. The 'resource' warning is due to
     * the PrintWriter in the doReturn statement. This resource is closed, because
     * it's technically injected in the save method, which closes the writer it
     * uses.
     */
    @Test
    @SuppressWarnings({ "PMD.AvoidUsingHardCodedIP", "resource" })
    public void testSave() throws Exception {
        ApplicationSettings settings = spy(ApplicationSettings.getInstance());
        settings.reset();
        File actual = new File("src/test/resources/settingsSaveActual.txt");
        File expected = new File("src/test/resources/settingsSaveExpected.txt");
        doReturn(new PrintWriter(new FileWriter(actual))).when(settings, "getWriter");
        settings.setRenderResolution(420, 65);
        settings.setVlcLocation("C:\\Test");
        settings.addCameraIP(new Camera().getNumber(), "420.420.420.420");
        settings.addCameraIP(new Camera().getNumber(), "65.65.65.65");
        settings.setDatabaseInfo("url", 1337, "pieter", "pjejnis", "password");
        settings.save();
        assertTrue(actual.exists());
        String eof = "\\A";
        Scanner sc = new Scanner(actual);
        Scanner sc2 = new Scanner(expected);
        sc.useDelimiter(eof);
        sc2.useDelimiter(eof);
        assertEquals(sc2.next(), sc.next());
        sc.close();
        sc2.close();
    }
    
    @Test
    @SuppressWarnings("resource")
    public void testSaveLoad() throws Exception {
        ApplicationSettings settings = spy(ApplicationSettings.getInstance());
        settings.reset();
        File file = new File("src/test/resources/settingsSaveLoad.txt");
        doReturn(new PrintWriter(new FileWriter(file))).when(settings, "getWriter");
        doReturn(new Scanner(file)).when(settings, "getScanner");
        settings.reset();
        settings.setVlcLocation("This is not a path ");
        settings.setDatabaseInfo("", 3306, "SwekJeweled", "", "pass");
        settings.addCameraIP(new Camera().getNumber(), "420.420.420.420");
        settings.save();
        settings.load();
        assertEquals("This is not a path", settings.getVlcLocation());
        assertEquals("420.420.420.420", settings.getCameraIP(0));
        assertEquals("SwekJeweled", settings.getDatabaseName());
        assertEquals(3306, settings.getDatabasePort());
        assertEquals("pass", settings.getDatabasePassword());
    }

}
