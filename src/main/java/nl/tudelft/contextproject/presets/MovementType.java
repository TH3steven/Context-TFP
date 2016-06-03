package nl.tudelft.contextproject.presets;

import nl.tudelft.contextproject.camera.CameraSettings;

import java.util.EnumMap;

/**
 * This enum represents different preset movements the user can select
 * and which the camera can apply.
 */
public enum MovementType {
    ZOOM_IN, ZOOM_OUT, LEFT, RIGHT, UP, DOWN, CUSTOM;

    private static EnumMap<MovementType, CameraSettings> typeToCameraSettings;
    private static EnumMap<MovementType, String> typeToName; 

    /**
     * Get the camera settings belonging to the MovementType.
     * 
     * @param mt The MovementType of which the camera settings are requested.
     * @return The camera settings for the type requested.
     */
    public static CameraSettings getCameraSettings(MovementType mt) {
        if (typeToCameraSettings == null) {
            initMapping();
        }

        return typeToCameraSettings.get(mt);
    }

    /**
     * Get the name corresponding to the MovementType.
     * 
     * @param mt The MovementType of which the name is requested.
     * @return The name for the type requested.
     */
    public static String getName(MovementType mt) {
        if (typeToCameraSettings == null) {
            initMapping();
        }

        return typeToName.get(mt);
    }

    /**
     * Initiate the different MovementTypes and put them in a mapping
     * along with their camera settings and their names.
     */
    private static void initMapping() {
        typeToCameraSettings = new EnumMap<>(MovementType.class);
        typeToName = new EnumMap<>(MovementType.class);

        typeToCameraSettings.put(MovementType.ZOOM_IN, new CameraSettings(0, 0, 0, 0));
        typeToCameraSettings.put(MovementType.ZOOM_OUT, new CameraSettings(0, 0, 0, 0));
        typeToCameraSettings.put(MovementType.LEFT, new CameraSettings(0, 0, 0, 0));
        typeToCameraSettings.put(MovementType.RIGHT, new CameraSettings(0, 0, 0, 0));
        typeToCameraSettings.put(MovementType.UP, new CameraSettings(0, 0, 0, 0));
        typeToCameraSettings.put(MovementType.DOWN, new CameraSettings(0, 0, 0, 0));
        typeToCameraSettings.put(MovementType.CUSTOM, new CameraSettings(0, 0, 0, 0));

        typeToName.put(MovementType.ZOOM_IN, "Constant zoom in");
        typeToName.put(MovementType.ZOOM_OUT, "Constant zoom out");
        typeToName.put(MovementType.LEFT, "Constant pan left");
        typeToName.put(MovementType.RIGHT, "Constant pan right");
        typeToName.put(MovementType.UP, "Constant tilt up");
        typeToName.put(MovementType.DOWN, "Constant tilt down");
        typeToName.put(MovementType.CUSTOM, "Custom");
    }
}
