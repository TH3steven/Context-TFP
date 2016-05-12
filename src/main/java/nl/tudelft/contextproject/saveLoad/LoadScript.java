package main.java.nl.tudelft.contextproject.saveLoad;

import main.java.nl.tudelft.contextproject.camera.Camera;
import main.java.nl.tudelft.contextproject.camera.CameraSettings;
import main.java.nl.tudelft.contextproject.presets.Preset;
import main.java.nl.tudelft.contextproject.script.Script;
import main.java.nl.tudelft.contextproject.script.Shot;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicReference;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

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
     *
     * @return the location of the save file this class loads from.
     */
    public static String getLoadLocation() {
        return loadLocation;
    }

    /**
     * Sets the location of the save file this class loads from.
     * Also creates a new instance of {@link #reader} so it may load from the
     * new load location when {@link #load()} is called.
     *
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
     *
     * @return the loaded script
     */
    public static Script load() throws XMLStreamException {
        synchronized (MUTEX) {
            reader = createReader();
            while (reader.hasNext()) {
                XMLEvent event = reader.nextEvent();
                if (event.isStartElement()) {
                    StartElement start = event.asStartElement();
                    if (start.getName().getLocalPart() == "cameras") {
                        loadCameras();
                    } else if (start.getName().getLocalPart() == "shots") {
                        loadShots();
                    }
                }
            }
            return new Script(new LinkedList<Shot>()); //TODO: Implement this method
        }
    }

    /**
     * Creates an XMLEventReader object using the file location specified
     * in {@link #loadLocation} for use as the loader class variable.
     * Throws a RuntimeException in the case the save file cannot be read.
     *
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

    private static void loadCameras() throws XMLStreamException {
        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement start = event.asStartElement();
                if (start.getName().getLocalPart() == "camera") {
                    loadCamera();
                } else {
                    throw new XMLStreamException("Unexpected start tag in cameras section: "
                            + start.getName().getLocalPart());
                }
            } else if (event.isEndElement()) {
                EndElement end = event.asEndElement();
                if (end.getName().getLocalPart() == "cameras") {
                    return;
                } else {
                    throw new XMLStreamException("Unexpected end tag in cameras section: "
                            + end.getName().getLocalPart());
                }
            }
        }
    }

    private static void loadCamera() throws XMLStreamException {
        Camera cam;
        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement start = event.asStartElement();
                if (start.getName().getLocalPart() == "cameraSettings") {
                    new Camera(loadCameraSettings(start));
                } else if (start.getName().getLocalPart() == "presets") {
                    cam = new Camera();
                    loadPresets(cam);
                }
            }
        }
    }

    private static CameraSettings loadCameraSettings(StartElement start) {
        return new CameraSettings(
                Integer.parseInt(start.getAttributeByName(new QName("pan")).getValue()),
                Integer.parseInt(start.getAttributeByName(new QName("tilt")).getValue()),
                Integer.parseInt(start.getAttributeByName(new QName("zoom")).getValue()),
                Integer.parseInt(start.getAttributeByName(new QName("focus")).getValue())
        );
    }

    // TODO this method has to be removed or updated.
    private static void loadPresets(Camera cam) throws XMLStreamException {
        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement start = event.asStartElement();
                if (start.getName().getLocalPart() == "preset") {
                    System.out.println("type" + cam.getPreset(Integer.parseInt(start.getAttributeByName(new QName("id")).getValue())).getClass().getSimpleName());
                } else if (start.getName().getLocalPart() == "id") {
                    System.out.println("PresetId is:" + cam.getPreset(Integer.parseInt(start.getAttributeByName(new QName("id")).getValue())));
                } else if (start.getName().getLocalPart() == "description") {
                    System.out.println("description is:" + cam.getPreset(Integer.parseInt(start.getAttributeByName(new QName("id")).getValue())));
                } else if (start.getName().getLocalPart() == "imgLoc") {
                    System.out.println("imageLoc is:" + cam.getPreset(Integer.parseInt(start.getAttributeByName(new QName("id")).getValue())).getImage());
                } else if (start.getName().getLocalPart() == "cameraSettings") {
                    cam = new Camera(loadCameraSettings(start));
                }
            }
        }
    }

    private static Preset loadPreset(StartElement presetStart) throws XMLStreamException, NoSuchMethodException, ClassNotFoundException {
        Class<?> c = Class.forName(presetStart.getAttributeByName(new QName("type")).getValue());
        Constructor<?> constructor = c.getConstructor(CameraSettings.class, Integer.class);
        int id = -1;
        String description = "";
        String imgLoc = "";
        AtomicReference<CameraSettings> toSet = new AtomicReference<CameraSettings>();
        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement start = event.asStartElement();
                if (start.getName().getLocalPart() == "id") {
                    XMLEvent idEvent = reader.nextEvent();
                    if (idEvent.isCharacters()) {
                        id = Integer.parseInt(idEvent.asCharacters().getData());
                    } else {
                        throw new XMLStreamException("Preset ID not preset.");
                    }
                } else if (start.getName().getLocalPart() == "description") {
                    XMLEvent descEvent = reader.nextEvent();
                    if (descEvent.isCharacters()) {
                        description = descEvent.asCharacters().getData();
                    }
                } else if (start.getName().getLocalPart() == "imgLoc") {
                    XMLEvent imgLocEvent = reader.nextEvent();
                    if (imgLocEvent.isCharacters()) {
                        imgLoc = imgLocEvent.asCharacters().getData();
                    }
                } else if (start.getName().getLocalPart() == "cameraSettings") {
                    toSet.set(loadCameraSettings(start));
                }
            }
            if (event.isEndElement()) {
                EndElement end = event.asEndElement();
                if (end.getName().getLocalPart() == "preset") {
                    break;
                }
            }
        }
        if (toSet.get() != null) {
            Preset preset;
            try {
                preset = (Preset) constructor.newInstance(toSet.get(), id);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                throw new XMLStreamException("Instantiating preset failed.", e);
            }
            preset.setDescription(description);
            preset.setImageLocation(imgLoc);
            return preset;
        } else {
            throw new XMLStreamException("No camera settings found in preset");
        }
    }

    private static void loadShots() throws XMLStreamException {
        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement start = event.asStartElement();
                if (start.getName().getLocalPart() == "shot") {
                    loadShot();
                } else {
                    throw new XMLStreamException("Unexpected start tag in shot section: "
                            + start.getName().getLocalPart());
                }
            } else if (event.isEndElement()) {
                EndElement end = event.asEndElement();
                if (end.getName().getLocalPart() == "shots") {
                    return;
                } else {
                    throw new XMLStreamException("Unexpected end tag in shots section: "
                            + end.getName().getLocalPart());
                }
            }
        }
    }

    private static void loadShot() throws XMLStreamException {
        int id = -1;
        int cameraId = -1;
        int presetId = -1;
        String description = "";
        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement start = event.asStartElement();
                if (start.getName().getLocalPart() == "Id") {
                    XMLEvent shotIdEvent = reader.nextEvent();
                    if (shotIdEvent.isCharacters()) {
                        id = Integer.parseInt(shotIdEvent.asCharacters().getData());
                    } else {
                        throw new XMLStreamException("Shot ID not present.");
                    }
                } else if (start.getName().getLocalPart() == "description") {
                    XMLEvent descEvent = reader.nextEvent();
                    if (descEvent.isCharacters()) {
                        description = descEvent.asCharacters().getData();
                    }
                } else if (start.getName().getLocalPart() == "cameraId") {
                    XMLEvent cameraIdEvent = reader.nextEvent();
                    if (cameraIdEvent.isCharacters()) {
                        cameraId = Integer.parseInt(cameraIdEvent.asCharacters().getData());
                    }
                } else if (start.getName().getLocalPart() == "PresetId") {
                    XMLEvent PresetIdEvent = reader.nextEvent();
                    if (PresetIdEvent.isCharacters()) {
                        presetId = Integer.parseInt(PresetIdEvent.asCharacters().getData());
                    }
                }
            }
        }
    }
}