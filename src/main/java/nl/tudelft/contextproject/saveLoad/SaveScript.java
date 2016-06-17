package nl.tudelft.contextproject.saveLoad;

import nl.tudelft.contextproject.camera.Camera;
import nl.tudelft.contextproject.camera.CameraSettings;
import nl.tudelft.contextproject.presets.Preset;
import nl.tudelft.contextproject.script.Script;
import nl.tudelft.contextproject.script.Shot;

import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;

/**
 * Utility class for saving a script to an XML file.
 * The location this file is to be saved to is stored in the private
 * variable {@link #saveLocation}, which can be set using {@link #setSaveLocation}.
 * 
 * @since 0.3
 */
public final class SaveScript {

    /**
     * Location of the save file to save to.
     * This is set to savefile.xml per default.
     */
    private static String saveLocation = "savefile.xml";
    
    private static final String TAG_CAMERA = "camera";
    private static final String TAG_CAMERAID = "cameraId";
    private static final String TAG_CAMERAS = "cameras";
    private static final String TAG_CAMERASETTINGS = "cameraSettings";
    private static final String TAG_DESCRIPTION = "description";
    private static final String TAG_ID = "id";
    private static final String TAG_IMGLOC = "imgLoc";
    private static final String TAG_PRESET = "preset";
    private static final String TAG_PRESETID = "presetId";
    private static final String TAG_PRESETS = "presets";
    private static final String TAG_SCRIPT = "script";
    private static final String TAG_SHOT = "shot";
    private static final String TAG_SHOTID = "shotId";
    private static final String TAG_SHOTS = "shots";
    private static final String TAG_ACTION = "action";

    /**
     * XMLEventWriter that writes everything away to an XML file.
     */
    private static XMLEventWriter writer;
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
     * @return The location of the save file this class saves to.
     */
    public static String getSaveLocation() {
        return saveLocation;
    }

    /**
     * Sets the location of the save file this class saves to.
     * Also creates a new instance of {@link #writer} so it may save to the
     * new save location when {@link #save(Script)} is called.
     * 
     * @param s the new location of the save file this class should save to.
     */
    public static void setSaveLocation(String s) {
        synchronized (MUTEX) {
            saveLocation = s;
        }
    }

    /**
     * Saves a script to an XML file at the location specified in
     * {@link #saveLocation}. This also saves the cameras currently found in
     * {@link Camera#CAMERAS}, including their defined presets.
     * 
     * @param script To be saved script.
     * @throws XMLStreamException In the case anything goes wrong.
     */
    public static void save(Script script) throws XMLStreamException {
        synchronized (MUTEX) {
            writer = createWriter();
            writer.add(eventFactory.createStartDocument());
            writer.add(eventFactory.createStartElement("", "", TAG_SCRIPT));
            generateCamerasSection();
            generateShotsSection(script);
            writer.add(eventFactory.createEndElement("", "", TAG_SCRIPT));
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
     * 
     * @return An XMLEventWriter object for use as the writer class variable.
     */
    private static XMLEventWriter createWriter() {
        try {
            return (XMLOutputFactory.newFactory()).createXMLEventWriter(new FileOutputStream(saveLocation), "UTF-8");
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
        writer.add(eventFactory.createStartElement("", "", TAG_CAMERAS));
        
        for (Camera cam : Camera.getAllCameras()) {
            generateCameraXML(cam);
        }
        
        writer.add(eventFactory.createEndElement("", "", TAG_CAMERAS));
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
        writer.add(eventFactory.createStartElement("", "", TAG_SHOTS));
        
        for (Shot shot1 : script.getShots()) {
            generateShotXML(shot1);
        }
        
        writer.add(eventFactory.createEndElement("", "", TAG_SHOTS));
    }

    /**
     * Generates and adds to the {@link #writer} the section of XML that
     * represents the specified camera.
     * 
     * @param cam The camera specified for which its XML should be added to the writer.
     * @throws XMLStreamException Thrown from {@link #writer}
     */
    private static void generateCameraXML(Camera cam) throws XMLStreamException {
        writer.add(eventFactory.createStartElement("", "", TAG_CAMERA));
        writer.add(eventFactory.createAttribute("id", cam.getNumber() + ""));
        generateCameraSettingsXML(cam.getSettings());
        writer.add(eventFactory.createStartElement("", "", TAG_PRESETS));
        
        for (Preset p : cam.getAllPresets()) {
            generatePresetXML(p);
        }
        
        writer.add(eventFactory.createEndElement("", "", TAG_PRESETS));
        writer.add(eventFactory.createEndElement("", "", TAG_CAMERA));
    }

    /**
     * Generates and adds to the {@link #writer} the section of XML that
     * represents the specified camera settings .
     * 
     * @param camSet The camera settings specified for which its XML should be added to the writer.
     * @throws XMLStreamException Thrown from {@link #writer}
     */
    private static void generateCameraSettingsXML(CameraSettings camSet) throws XMLStreamException {
        writer.add(eventFactory.createStartElement("", "", TAG_CAMERASETTINGS));
        writer.add(eventFactory.createAttribute("pan", camSet.getPan() + ""));
        writer.add(eventFactory.createAttribute("tilt", camSet.getTilt() + ""));
        writer.add(eventFactory.createAttribute("zoom", camSet.getZoom() + ""));
        writer.add(eventFactory.createAttribute("focus", camSet.getFocus() + ""));
        writer.add(eventFactory.createEndElement("", "", TAG_CAMERASETTINGS));
    }

    /**
     * Generates and adds to the {@link #writer} the section of XML that
     * represents the specified preset.
     * 
     * @param preset The preset specified for which its XML should be added to the writer.
     * @throws XMLStreamException Thrown from {@link #writer}
     */
    private static void generatePresetXML(Preset preset) throws XMLStreamException {
        writer.add(eventFactory.createStartElement("", "", TAG_PRESET));
        writer.add(eventFactory.createAttribute("type", preset.getClass().getName()));
        writer.add(eventFactory.createStartElement("", "", TAG_ID));
        writer.add(eventFactory.createCharacters(preset.getId() + ""));
        writer.add(eventFactory.createEndElement("", "", TAG_ID));
        writer.add(eventFactory.createStartElement("", "", TAG_DESCRIPTION));
        writer.add(eventFactory.createCharacters(preset.getDescription()));
        writer.add(eventFactory.createEndElement("", "", TAG_DESCRIPTION));
        writer.add(eventFactory.createStartElement("", "", TAG_IMGLOC));
        writer.add(eventFactory.createCharacters(preset.getImage()));
        writer.add(eventFactory.createEndElement("", "", TAG_IMGLOC));
        generateCameraSettingsXML(preset.getToSet());
        writer.add(eventFactory.createEndElement("", "", TAG_PRESET));
    }

    /**
     * Generates and adds to the {@link #writer} the section of XML that
     * represents the specified shot.
     * 
     * @param shot The shot specified for which its XML should be added to the writer.
     * @throws XMLStreamException Thrown from {@link #writer}
     */
    private static void generateShotXML(Shot shot) throws XMLStreamException {
        writer.add(eventFactory.createStartElement("", "", TAG_SHOT));
        writer.add(eventFactory.createAttribute("number", shot.getNumber() + ""));
        writer.add(eventFactory.createStartElement("", "", TAG_SHOTID));
        writer.add(eventFactory.createCharacters(shot.getShotId()));
        writer.add(eventFactory.createEndElement("", "", TAG_SHOTID));
        writer.add(eventFactory.createStartElement("", "", TAG_DESCRIPTION));
        writer.add(eventFactory.createCharacters(shot.getDescription()));
        writer.add(eventFactory.createEndElement("", "", TAG_DESCRIPTION));
        writer.add(eventFactory.createStartElement("", "", TAG_ACTION));
        writer.add(eventFactory.createCharacters(shot.getAction()));
        writer.add(eventFactory.createEndElement("", "", TAG_ACTION));
        writer.add(eventFactory.createStartElement("", "", TAG_CAMERAID));
        writer.add(eventFactory.createCharacters(shot.getCamera().getNumber() + ""));
        writer.add(eventFactory.createEndElement("", "", TAG_CAMERAID));
        writer.add(eventFactory.createStartElement("", "", TAG_PRESETID));
        if (shot.getPreset() != null) {
            writer.add(eventFactory.createCharacters(shot.getPreset().getId() + ""));
        }
        writer.add(eventFactory.createEndElement("", "", TAG_PRESETID));
        writer.add(eventFactory.createEndElement("", "", TAG_SHOT));
    }
}
