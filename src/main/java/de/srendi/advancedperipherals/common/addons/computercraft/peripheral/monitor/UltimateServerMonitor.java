/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2022. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */
package de.srendi.advancedperipherals.common.addons.computercraft.peripheral.monitor;

import com.google.common.annotations.VisibleForTesting;
import dan200.computercraft.core.terminal.Terminal;
import dan200.computercraft.shared.util.TickScheduler;
import de.srendi.advancedperipherals.common.addons.computercraft.terminal.UltimateNetworkedTerminal;

import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicBoolean;

public class UltimateServerMonitor {
    private final UltimateMonitorEntity origin;

    private int textScale = 10;
    private @Nullable UltimateNetworkedTerminal terminal;
    private final AtomicBoolean resized = new AtomicBoolean( false );
    private final AtomicBoolean changed = new AtomicBoolean( false );

    UltimateServerMonitor(UltimateMonitorEntity origin) {
        this.origin = origin;
    }

    synchronized void rebuild() {
        Terminal oldTerm = getTerminal();
        int oldWidth = oldTerm == null ? -1 : oldTerm.getWidth();
        int oldHeight = oldTerm == null ? -1 : oldTerm.getHeight();

        double textScale = this.textScale * 0.1;
        int termWidth = (int) Math.max(
            Math.round( (origin.getWidth() - 2.0 * (UltimateMonitorEntity.RENDER_BORDER + UltimateMonitorEntity.RENDER_MARGIN)) / (textScale * 6.0 * UltimateMonitorEntity.RENDER_PIXEL_SCALE) ),
            1.0
        );
        int termHeight = (int) Math.max(
            Math.round( (origin.getHeight() - 2.0 * (UltimateMonitorEntity.RENDER_BORDER + UltimateMonitorEntity.RENDER_MARGIN)) / (textScale * 9.0 * UltimateMonitorEntity.RENDER_PIXEL_SCALE) ),
            1.0
        );

        if (terminal == null) {
            terminal = new UltimateNetworkedTerminal(termWidth, termHeight, this::markChanged);
            markChanged();
        } else {
            terminal.resize(termWidth, termHeight);
        }

        if (oldWidth != termWidth || oldHeight != termHeight) {
            terminal.clear();
            resized.set(true);
            markChanged();
        }
    }

    private void markChanged() {
        if (!changed.getAndSet(true)) {
            TickScheduler.schedule(origin.tickToken);
        }
    }

    int getTextScale() {
        return textScale;
    }

    synchronized void setTextScale(int textScale) {
        if (this.textScale == textScale) {
            return;
        }
        this.textScale = textScale;
        rebuild();
    }

    boolean pollResized() {
        return resized.getAndSet(false);
    }

    boolean pollTerminalChanged() {
        return changed.getAndSet(false);
    }

    @Nullable
    @VisibleForTesting
    public UltimateNetworkedTerminal getTerminal() {
        return terminal;
    }
}
