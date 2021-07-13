package de.srendi.advancedperipherals.common.util;

import java.io.IOException;
import java.io.OutputStream;

public class CountingWipingStream extends OutputStream {
    private int counter = 0;

    public int getWrittenBytes() {
        return counter;
    }

    @Override
    public void write(int i) throws IOException {
        counter += 1;
    }
}
