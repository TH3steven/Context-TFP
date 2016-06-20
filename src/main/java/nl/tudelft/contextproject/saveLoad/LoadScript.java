package nl.tudelft.contextproject.saveLoad;

import nl.tudelft.contextproject.camera.Camera;
import nl.tudelft.contextproject.camera.CameraSettings;
import nl.tudelft.contextproject.presets.Preset;
import nl.tudelft.contextproject.script.Script;
import nl.tudelft.contextproject.script.Shot;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 * Utility class for loading a script from an XML file.
 * The location this file is to be loaded from is stored in the private
 * variable {@link #loadLocation}, which can be set using {@link #setLoadLocation}.
 * 
 * <p>This class has a high cyclomatic complexity due to the load methods.
 * We did not find any good way to get around this, without sacrificing
 * code readability.
 * 
 * @since 0.3
 */
public final class LoadScript {

    private static final Object MUTEX = new Object();
    
    /**
     * Location of the save file to load from.
     * This is set to savefile.xml per default.
     */
    private static String loadLocation = "savefile.xml";

    /**
     * XMLEventReader that reads from a save file.
     */
    private static XMLEventReader reader;

    /**
     * Since this is a utility class, the constructor may not be called.
     */
    private LoadScript() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the location of the save file this class loads from.
     * @return The location of the save file this class loads from.
     */
    public static String getLoadLocation() {
        return loadLocation;
    }

    /**
     * Sets the location of the save file this class loads from.
     * Also creates a new instance of {@link #reader} so it may load from the
     * new load location when {@link #load()} is called.
     *
     * @param s The new location of the save file this class should load from.
     */
    public static void setLoadLocation(String s) {
        synchronized (MUTEX) {
            loadLocation = s;
        }
    }

    /**
     * Loads a script from the XML file at the location specified in
     * {@link #loadLocation}.
     * It loads the cameras from the save file and puts them in {@link Camera#CAMERAS}.
     * It then loads the shots from the save file and returns them as a Script object.
     * 
     * @return The loaded script
     */
    public static Script load() throws XMLStreamException {
        synchronized (MUTEX) {
            Camera.clearAllCameras();
            List<Shot> shots = new LinkedList<Shot>();
            reader = createReader();
            checkCorrectDocument();
            
            while (reader.hasNext()) {
                XMLEvent event = reader.nextEvent();
                if (event.isStartElement()) {
                    StartElement start = event.asStartElement();
                    if ("cameras".equals(start.getName().getLocalPart())) {
                        loadCameras();
                    } else if ("shots".equals(start.getName().getLocalPart())) {
                        shots = loadShots();
                    }
                }
            }
            
            return new Script(shots);
        }
    }
    
    /**
     * Checks if the document to be read is actually a save file from our
     * application. Does this by reading the first two events in the file,
     * namely the start of the document and the first tag afterwards,
     * which should be 'script'.
     * @throws XMLStreamException When the save file is not correct.
     */
    private static void checkCorrectDocument() throws XMLStreamException {
        if (reader.hasNext()) {
            reader.nextEvent(); //skip start of document
        }
        if (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement() 
                    && "script".equals(event.asStartElement().getName().getLocalPart())) {
                return;
            }
        }
        throw new XMLStreamException("This is not a savefile from our program.");
    }

    /**
     * Creates an XMLEventReader object using the file location specified
     * in {@link #loadLocation} for use as the loader class variable.
     * Throws a RuntimeException in the case the save file cannot be read.
     *
     * @return An XMLEventWriter object for use as the writer class variable.
     */
    private static XMLEventReader createReader() {
        try {
            return (XMLInputFactory.newFactory()).createXMLEventReader(new FileInputStream(loadLocation), "UTF-8");
        } catch (IOException | XMLStreamException e) {
            e.printStackTrace();
            throw new RuntimeException("Your save file could not be found or read.", e);
        }
    }

    /**
     * Reads the 'cameras' section of the XML file.
     * Assumes that the start element of this section has already been read.
     * 
     * @throws XMLStreamException when an error occurs in the XML.
     */
    private static void loadCameras() throws XMLStreamException {
        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement start = event.asStartElement();
                if ("camera".equals(start.getName().getLocalPart())) {
                    loadCamera(Integer.parseInt(start.getAttributeByName(new QName("id")).getValue()));
                } else {
                    throw new XMLStreamException("Unexpected start tag in cameras section: "
                            + start.getName().getLocalPart());
                }
            } else if (event.isEndElement()) {
                EndElement end = event.asEndElement();
                if ("cameras".equals(end.getName().getLocalPart())) {
                    return;
                }
                throw new XMLStreamException("Unexpected end tag in cameras section: "
                        + end.getName().getLocalPart());
                
            }
        }
    }

    /**
     * Reads a 'camera' section of the XML file.
     * Assumes that the start element of this section has already been read.
     * 
     * @param id Id of the camera to be loaded.
     * @throws XMLStreamException when an error occurs in the XML.
     */
    private static void loadCamera(int id) throws XMLStreamException {
        Camera cam = Camera.getCamera(id) == null ? new Camera() : Camera.getCamera(id);
        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement start = event.asStartElement();
                if ("cameraSettings".equals(start.getName().getLocalPart())) {
                    cam.setSettings(loadCameraSettings(start));
                } else if ("presets".equals(start.getName().getLocalPart())) {
                    loadPresets(cam);
                }
            }
            if (event.isEndElement()) {
                EndElement end = event.asEndElement();
                if ("camera".equals(end.getName().getLocalPart())) {
                    return;
                }
            }
        }
    }

    /**
     * Reads a 'cameraSettings' section of the XML file.
     * Assumes that the start element of this section has already been read
     * and is inserted into this method as the start parameter.
     * 
     * @param start The start element of the 'cameraSettings' section.
     * @return The loaded CameraSettings object.
     * @throws XMLStreamException when an error occurs in the XML.
     */
    private static CameraSettings loadCameraSettings(StartElement start) {
        return new CameraSettings(
                Integer.parseInt(start.getAttributeByName(new QName("pan")).getValue()),
                Integer.parseInt(start.getAttributeByName(new QName("tilt")).getValue()),
                Integer.parseInt(start.getAttributeByName(new QName("zoom")).getValue()),
                Integer.parseInt(start.getAttributeByName(new QName("focus")).getValue())
                );
    }

    /**
     * Reads a 'presets' section associated with a camera of the XML file and
     * adds these presets to the camera they belong to.
     * Assumes that the start element of this section has already been read.
     * 
     * @param cam Camera object associated with this 'presets' section.
     * @throws XMLStreamException when an error occurs in the XML.
     */
    private static void loadPresets(Camera cam) throws XMLStreamException {
        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement start = event.asStartElement();
                if ("preset".equals(start.getName().getLocalPart())) {
                    cam.addPreset(loadPreset(start));
                }
            } else if (event.isEndElement()) {
                EndElement end = event.asEndElement();
                if ("presets".equals(end.getName().getLocalPart())) {
                    break;
                }
            }
        }
    }

    /**
     * Reads a 'preset' section of the XML file.
     * Assumes that the start element of this section has already been read
     * and is inserted into this method as the presetStart parameter.
     * 
     * @param presetStart The start element of the 'preset' section.
     * @return The loaded Preset object.
     * @throws XMLStreamException when an error occurs in the XML.
     */
    private static Preset loadPreset(StartElement presetStart) throws XMLStreamException {
        int id = -1;
        String description = "";
        String imgLoc = "";
        AtomicReference<CameraSettings> toSet = new AtomicReference<CameraSettings>();
        
        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement start = event.asStartElement();
                if ("id".equals(start.getName().getLocalPart())) {
                    XMLEvent idEvent = reader.nextEvent();
                    if (idEvent.isCharacters()) {
                        id = Integer.parseInt(idEvent.asCharacters().getData());
                    } else {
                        throw new XMLStreamException("Preset ID not preset.");
                    }
                } else if ("description".equals(start.getName().getLocalPart())) {
                    XMLEvent descEvent = reader.nextEvent();
                    if (descEvent.isCharacters()) {
                        description = descEvent.asCharacters().getData();
                    }
                } else if ("imgLoc".equals(start.getName().getLocalPart())) {
                    XMLEvent imgLocEvent = reader.nextEvent();
                    if (imgLocEvent.isCharacters()) {
                        imgLoc = imgLocEvent.asCharacters().getData();
                    }
                } else if ("cameraSettings".equals(start.getName().getLocalPart())) {
                    toSet.set(loadCameraSettings(start));
                }
            }
            
            if (event.isEndElement()) {
                EndElement end = event.asEndElement();
                if ("preset".equals(end.getName().getLocalPart())) {
                    break;
                }
            }
        }
        if (toSet.get() != null) {
            String type = presetStart.getAttributeByName(new QName("type")).getValue();
            return createPreset(type, toSet.get(), id, description, imgLoc);
        }
        throw new XMLStreamException("No camera settings found in preset");
    }
    
    /**
     * Gets the constructor from the right preset class as defined by parameter 'type' and
     * returns the Preset object instantiated with the rest of the arguments.
     * 
     * @param type Full name of the preset class, as returned by {@link Class#getName()}
     * @param toSet CameraSettings to set in the preset.
     * @param id Id of the preset.
     * @param description Description of the preset.
     * @param imgLoc Image location of the preset.
     * @return The constructed preset according to the parameters given.
     * @throws XMLStreamException when instantiating the preset fails.
     * @see {@link Preset#Preset}
     */
    private static Preset createPreset(String type, CameraSettings toSet, int id, 
            String description, String imgLoc) throws XMLStreamException {
        Preset preset;
        
        try {
            Constructor<?> constructor = Class.forName(type).getConstructor(CameraSettings.class, int.class);
            preset = (Preset) constructor.newInstance(toSet, id);
        } catch (Exception e) {
            e.printStackTrace();
            throw new XMLStreamException("Instantiating preset failed.", e);
        }
        
        preset.setDescription(description);
        preset.setImageLocation(imgLoc);
        return preset;
    }

    /**
     * Reads the 'shots' section of the XML file and returns this as a list
     * of Shot objects.
     * Assumes that the start element of this section has already been read.
     * 
     * @return The loaded list of shots.
     * @throws XMLStreamException when an error occurs in the XML.
     */
    private static List<Shot> loadShots() throws XMLStreamException {
        List<Shot> shots = new LinkedList<Shot>();
        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement start = event.asStartElement();
                if ("shot".equals(start.getName().getLocalPart())) {
                    shots.add(loadShot(start));
                } else {
                    throw new XMLStreamException("Unexpected start tag in shot section: "
                            + start.getName().getLocalPart());
                }
            } else if (event.isEndElement()) {
                EndElement end = event.asEndElement();
                if ("shots".equals(end.getName().getLocalPart())) {
                    break;
                }
                throw new XMLStreamException("Unexpected end tag in shots section: "
                        + end.getName().getLocalPart());
                
            }
        }
        return shots;
    }

    /**
     * Reads a 'shot' section of the XML file and returns this as a Shot object.
     * Assumes that the start element of this section has already been read and
     * is inserted into this method as the startShot parameter.
     * 
     * @return The loaded shot.
     * @throws XMLStreamException when an error occurs in the XML.
     */
    private static Shot loadShot(StartElement startShot) throws XMLStreamException {
        int id = Integer.parseInt(startShot.getAttributeByName(new QName("number")).getValue());
        String shotId = "";
        int cameraId = -1;
        int presetId = -1;
        String description = "";
        String action = "";
        
        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement start = event.asStartElement();
                if ("shotId".equals(start.getName().getLocalPart())) {
                    XMLEvent shotIdEvent = reader.nextEvent();
                    if (shotIdEvent.isCharacters()) {
                        shotId = shotIdEvent.asCharacters().getData();
                    }
                } else if ("description".equals(start.getName().getLocalPart())) {
                    XMLEvent descEvent = reader.nextEvent();
                    if (descEvent.isCharacters()) {
                        description = descEvent.asCharacters().getData();
                    }
                } else if ("cameraId".equals(start.getName().getLocalPart())) {
                    XMLEvent cameraIdEvent = reader.nextEvent();
                    if (cameraIdEvent.isCharacters()) {
                        cameraId = Integer.parseInt(cameraIdEvent.asCharacters().getData());
                    } else {
                        throw new XMLStreamException("No camera id present in shot.");
                    }
                } else if ("presetId".equals(start.getName().getLocalPart())) {
                    XMLEvent presetIdEvent = reader.nextEvent();
                    if (presetIdEvent.isCharacters()) {
                        presetId = Integer.parseInt(presetIdEvent.asCharacters().getData());
                    }
                } else if ("action".equals(start.getName().getLocalPart())) {
                    XMLEvent actionEvent = reader.nextEvent();
                    if (actionEvent.isCharacters()) {
                        action = actionEvent.asCharacters().getData();
                    }
                }
            }
            if (event.isEndElement()) {
                EndElement end = event.asEndElement();
                if ("shot".equals(end.getName().getLocalPart())) {
                    break;
                }
            }
        }
        
        Camera cam = Camera.getCamera(cameraId);
        if (cam != null) {
            return new Shot(id, shotId, cam, cam.getPreset(presetId), description, action);
        }
        throw new XMLStreamException("Camera cannot be found with camera id: " + cameraId);
    }
}
