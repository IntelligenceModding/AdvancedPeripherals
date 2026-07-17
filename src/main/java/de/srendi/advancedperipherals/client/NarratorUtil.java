package de.srendi.advancedperipherals.client;

import com.mojang.text2speech.Narrator;

public class NarratorUtil {
    private static final Narrator NARRATOR = Narrator.getNarrator();

    private NarratorUtil() {}

    public static void say(String message, boolean interrupt) {
        if (interrupt) {
            NARRATOR.clear();
        }
        NARRATOR.say(message, interrupt);
    }
}
