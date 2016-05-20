package main.java.nl.tudelft.contextproject.presets;

import main.java.nl.tudelft.contextproject.camera.CameraSettings;

import java.util.EnumMap;

public enum MovementType {
    ZOOM_IN, ZOOM_OUT, LEFT, RIGHT, UP, DOWN, CUSTOM;
    
    private static EnumMap<MovementType, CameraSettings> typeToCameraSettings;
    private static EnumMap<MovementType, String> typeToName; 
    
    public static CameraSettings getCameraSettings(MovementType mt) {
        if (typeToCameraSettings == null) {
            initMapping();
        }
        return typeToCameraSettings.get(mt);
    }
    
    private static void initMapping() {
        typeToCameraSettings = new EnumMap<>(MovementType.class);
        typeToName = new EnumMap<>(MovementType.class);
     
        typeToCameraSettings.put(MovementType.ZOOM_IN, new CameraSettings(0, 0, CameraSettings.ZOOM_LIMIT_HIGH, 0));
        typeToCameraSettings.put(MovementType.ZOOM_OUT, new CameraSettings(0, 0, 0, 0));
        typeToCameraSettings.put(MovementType.LEFT, new CameraSettings(0, 0, 0, 0));
        typeToCameraSettings.put(MovementType.RIGHT, new CameraSettings(CameraSettings.PAN_LIMIT_HIGH, 0, 0, 0));
        typeToCameraSettings.put(MovementType.UP, new CameraSettings(0, CameraSettings.PAN_LIMIT_HIGH, 0, 0));
        typeToCameraSettings.put(MovementType.DOWN, new CameraSettings(0, CameraSettings.PAN_LIMIT_LOW, 0, 0));
        typeToCameraSettings.put(MovementType.CUSTOM, new CameraSettings());
        
        typeToName.put(MovementType.ZOOM_IN, "Constant zoom in");
        typeToName.put(MovementType.ZOOM_OUT, "Constant zoom out");
        typeToName.put(MovementType.LEFT, "Constant pan left");
        typeToName.put(MovementType.RIGHT, "Constant pan right");
        typeToName.put(MovementType.UP, "Constant tilt up");
        typeToName.put(MovementType.DOWN, "Constant tilt down");
        typeToName.put(MovementType.CUSTOM, "Custom");

    }
}
