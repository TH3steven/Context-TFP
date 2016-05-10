package main.java.nl.tudelft.contextproject.saveLoad;

import main.java.nl.tudelft.contextproject.camera.Camera;
import main.java.nl.tudelft.contextproject.script.Script;

/**
 * Utility class for saving a script to an XML file.
 * The location this file is to be saved to is stored in the private
 * variable {@link saveLocation}, which can be set using {@link #setSaveLocation}.
 * 
 * @author Bart van Oort
 * @since 0.3
 */
public final class SaveScript {
    
    private static String saveLocation;
    
    /**
     * Since this is a utility class, the constructor may not be called.
     */
    private SaveScript() {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Returns the location of the save file this class saves to.
     * @return the location of the save file this class saves to.
     */
    public static String getSaveLocation() {
        return saveLocation;
    }
    
    /**
     * Sets the location of the save file this class saves to.
     * @param s the new location of the save file this class should save to.
     */
    public static void setSaveLocation(String s) {
        saveLocation = s;
    }
    
    /**
     * Saves a script to an XML file at the location specified in
     * {@link #saveLocation}. This also saves the cameras currently found in
     * {@link Camera#CAMERAS}, including their defined presets.
     * @param script To be saved script.
     */
    public static void save(Script script) {
        //TODO: Implement this method.
    }
}
