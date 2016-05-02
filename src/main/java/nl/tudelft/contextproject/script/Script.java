package main.java.nl.tudelft.contextproject.script;

import java.util.Iterator;
import java.util.List;

import main.java.nl.tudelft.contextproject.presets.Preset;

/**
 * Class to represent a script of presets.
 * Implements the Iterator interface so it can apply
 * presets as the list of presets is being traversed.
 * 
 * @author Bart van Oort
 * @since 0.2
 */
public class Script implements Iterator<Preset> {
    
    private List<Preset> presets;
    private int current;
    
    /**
     * Creates a script that starts from the beginning
     * with specified presets.
     * @param presets
     */
    public Script(List<Preset> presets) {
        this.presets = presets;
        current = 0;
    }

    @Override
    public boolean hasNext() {
        return current < presets.size();
    }

    /**
     * Does what {@link Iterator#next} does, but also
     * applies the preset while doing so.
     */
    @Override
    public Preset next() {
        Preset p = presets.get(current);
        p.apply();
        current++;
        return p;
    }
}
