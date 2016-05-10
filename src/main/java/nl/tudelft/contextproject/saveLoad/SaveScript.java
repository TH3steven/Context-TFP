package main.java.nl.tudelft.contextproject.saveLoad;

import main.java.nl.tudelft.contextproject.camera.Camera;
import main.java.nl.tudelft.contextproject.script.Script;

import java.io.FileWriter;
import java.io.IOException;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;

/**
 * Utility class for saving a script to an XML file.
 * The location this file is to be saved to is stored in the private
 * variable {@link saveLocation}, which can be set using {@link #setSaveLocation}.
 * 
 * @since 0.3
 */
public final class SaveScript {
    
    /**
     * Location of the save file to save to.
     * This is set to savefile.xml per default.
     */
    private static String saveLocation = "savefile.xml";
    
    /**
     * XMLEventWriter that writes everything away to an XML file.
     */
    private static XMLEventWriter writer = createWriter();
    private static XMLEventFactory eventFactory = XMLEventFactory.newFactory();
    
    private static final Object MUTEX = new Object();
    
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
     * Also creates a new instance of {@link writer} so it may save to the
     * new save location when {@link #save(Script)} is called.
     * @param s the new location of the save file this class should save to.
     */
    public static void setSaveLocation(String s) {
        synchronized (MUTEX) {
            saveLocation = s;
            writer = createWriter();
        }
    }
    
    /**
     * Saves a script to an XML file at the location specified in
     * {@link #saveLocation}. This also saves the cameras currently found in
     * {@link Camera#CAMERAS}, including their defined presets.
     * @param script To be saved script.
     * @throws XMLStreamException In the case anything goes wrong.
     */
    public static void save(Script script) throws XMLStreamException {
        synchronized (MUTEX) {
            writer.add(eventFactory.createStartDocument());
            generateCamerasSection();
            generateShotsSection(script);
            writer.add(eventFactory.createEndDocument());
            writer.flush();
            writer.close();
        }
    }
    
    /**
     * Creates an XMLEventWriter object using the file location specified
     * in {@link #saveLocation} for use as the writer class variable.
     * May throw a RuntimeException in the case something goes wrong in 
     * creating the save file.
     * @return an XMLEventWriter object for use as the writer class variable.
     */
    private static XMLEventWriter createWriter() {
        try {
            return (XMLOutputFactory.newFactory()).createXMLEventWriter(new FileWriter(saveLocation));
        } catch (IOException | XMLStreamException e) {
            throw new RuntimeException("Something went wrong in creating your save file", e);
        }
    }
    
    /**
     * Generates and adds to the {@link #writer} the section of XML that
     * represents the cameras.
     * 
     * <p><table border="2" cellpadding="4">
     *    <thead><tr><th>WARNING</th></tr></thead>
     *    <tdata><tr>
     *       <td>This method should only be called from within {@link #save(Script)}!!!</td>
     *    </tr></tdata>
     * </table>
     * @throws XMLStreamException Thrown from {@link #writer}
     */
    private static void generateCamerasSection() throws XMLStreamException {
        writer.add(eventFactory.createStartElement("", "", "cameras"));
        //TODO: Add individual cameras
        writer.add(eventFactory.createEndElement("", "", "cameras"));
    }
    
    /**
     * Generates and adds to the {@link #writer} the section of XML that
     * represents the list of shots found within a script.
     * 
     * <p><table border="2" cellpadding="4">
     *    <thead><tr><th>WARNING</th></tr></thead>
     *    <tdata><tr>
     *       <td>This method should only be called from within {@link #save(Script)}!!!</td>
     *    </tr></tdata>
     * </table>
     * @throws XMLStreamException Thrown from {@link #writer}
     */
    private static void generateShotsSection(Script script) throws XMLStreamException {
        writer.add(eventFactory.createStartElement("", "", "shots"));
        //TODO: Add shots
        writer.add(eventFactory.createEndElement("", "", "shots"));
    }
}
