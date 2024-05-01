package de.srendi.advancedperipherals;

import com.diffplug.spotless.FormatterFunc;
import com.diffplug.spotless.FormatterStep;

import java.io.Serializable;

// Just a step for me to play around. Could evolve into something bigger when this branch gets merged into production
public class JustPlayingAroundStep {

    public static FormatterStep create() {
        return FormatterStep.create("", new State(), State::toFormatter);
    }

    private static class State implements Serializable {
        FormatterFunc toFormatter() {
            return input -> input;
        }
    }
}
