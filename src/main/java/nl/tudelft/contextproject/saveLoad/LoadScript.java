package main.java.nl.tudelft.contextproject.saveLoad;

import main.java.nl.tudelft.contextproject.script.Script;
import main.java.nl.tudelft.contextproject.script.Shot;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

public final class LoadScript {
    
    /**
     * Location of the save file to load from.
     * This is set to savefile.xml per default.
     */
    private static String loadLocation = "savefile.xml";
    
    private static XMLEventReader reader;
    
    private static final Object MUTEX = new Object();
    
    /**
     * Since this is a utility class, the constructor may not be called.
     */
    private LoadScript() {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Returns the location of the save file this class loads from.
     * @return the location of the save file this class loads from.
     */
    public static String getLoadLocation() {
        return loadLocation;
    }
    
    /**
     * Sets the location of the save file this class loads from.
     * Also creates a new instance of {@link reader} so it may load from the
     * new load location when {@link #load()} is called.
     * @param s the new location of the save file this class should load from.
     */
    public static void setLoadLocation(String s) {
        synchronized (MUTEX) {
            loadLocation = s;
        }
    }
    
    /**
     * Loads a script from the XML file at the location specified in
     * {@link #loadLocation}.
     * @return the loaded script
     */
    public static Script load() {
        synchronized (MUTEX) {
            reader = createReader();
            return new Script(new LinkedList<Shot>()); //TODO: Implement this method
        }
    }
    
    /**
     * Creates an XMLEventReader object using the file location specified
     * in {@link #loadLocation} for use as the loader class variable.
     * Throws a RuntimeException in the case the save file cannot be read.
     * @return an XMLEventWriter object for use as the writer class variable.
     */
    private static XMLEventReader createReader() {
        try {
            return (XMLInputFactory.newFactory()).createXMLEventReader(new FileInputStream(loadLocation));
        } catch (IOException | XMLStreamException e) {
            e.printStackTrace();
            throw new RuntimeException("Your save file could not be found or read.", e);
        }
    }
    
}
