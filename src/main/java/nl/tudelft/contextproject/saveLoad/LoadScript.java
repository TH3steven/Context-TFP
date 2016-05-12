package main.java.nl.tudelft.contextproject.saveLoad;

import main.java.nl.tudelft.contextproject.camera.Camera;
import main.java.nl.tudelft.contextproject.camera.CameraSettings;
import main.java.nl.tudelft.contextproject.presets.Preset;
import main.java.nl.tudelft.contextproject.script.Script;
import main.java.nl.tudelft.contextproject.script.Shot;

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
            Camera.clearAllCameras();
            List<Shot> shots = new LinkedList<Shot>();
            reader = createReader();
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
                if ("camera".equals(start.getName().getLocalPart())) {
                    loadCamera();
                } else {
                    throw new XMLStreamException("Unexpected start tag in cameras section: "
                            + start.getName().getLocalPart());
                }
            } else if (event.isEndElement()) {
                EndElement end = event.asEndElement();
                if ("cameras".equals(end.getName().getLocalPart())) {
                    return;
                } else {
                    throw new XMLStreamException("Unexpected end tag in cameras section: "
                            + end.getName().getLocalPart());
                }
            }
        }
    }

    private static void loadCamera() throws XMLStreamException {
        Camera cam = new Camera();
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
            Preset preset;
            try {
                Class<?> c = Class.forName(presetStart.getAttributeByName(new QName("type")).getValue());
                Constructor<?> constructor = c.getConstructor(CameraSettings.class, int.class);
                preset = (Preset) constructor.newInstance(toSet.get(), id);
            } catch (Exception e) {
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
                } else {
                    throw new XMLStreamException("Unexpected end tag in shots section: "
                            + end.getName().getLocalPart());
                }
            }
        }
        return shots;
    }

    private static Shot loadShot(StartElement startShot) throws XMLStreamException {
        int id = Integer.parseInt(startShot.getAttributeByName(new QName("number")).getValue());
        String shotId = "";
        int cameraId = -1;
        int presetId = -1;
        String description = "";
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
                    } else {
                        throw new XMLStreamException("No preset id present in shot.");
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
        if (cam != null && cam.getPreset(presetId) != null) {
            return new Shot(id, shotId, cam, cam.getPreset(presetId), description);
        } else {
            throw new XMLStreamException("Camera or preset can be found with camera id: " + cameraId
                    + " and preset id: " + presetId);
        }
    }
}
