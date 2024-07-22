// Copyright Daniel Ratcliffe, 2011-2022. Do not distribute without permission.
//
// SPDX-License-Identifier: LicenseRef-CCPL

package de.srendi.advancedperipherals.common.addons.computercraft.terminal;

import dan200.computercraft.core.terminal.Terminal;
import dan200.computercraft.shared.peripheral.monitor.ServerMonitor;
import dan200.computercraft.shared.util.TickScheduler;
import de.srendi.advancedperipherals.common.blocks.blockentities.UltimateMonitorEntity;

import org.jetbrains.annotations.Nullable;
import java.util.concurrent.atomic.AtomicBoolean;

public class UltimateServerMonitor extends ServerMonitor {
    private final UltimateMonitorEntity origin;

    private @Nullable UltimateNetworkedTerminal terminal;
    private final AtomicBoolean resized = new AtomicBoolean(false);

    public UltimateServerMonitor(UltimateMonitorEntity origin) {
        super(true, origin);
    }

    @Override
    synchronized void rebuild() {
        Terminal oldTerm = getTerminal();
        var oldWidth = oldTerm == null ? -1 : oldTerm.getWidth();
        var oldHeight = oldTerm == null ? -1 : oldTerm.getHeight();

        var textScale = this.getTextScale() * 0.5;
        var termWidth = (int) Math.max(
            (double) Math.round((origin.getWidth() - 2.0 * (UltimateMonitorEntity.RENDER_BORDER + UltimateMonitorEntity.RENDER_MARGIN)) / (textScale * 6.0 * UltimateMonitorEntity.RENDER_PIXEL_SCALE)),
            1.0
        );
        var termHeight = (int) Math.max(
            (double) Math.round((origin.getHeight() - 2.0 * (UltimateMonitorEntity.RENDER_BORDER + UltimateMonitorEntity.RENDER_MARGIN)) / (textScale * 9.0 * UltimateMonitorEntity.RENDER_PIXEL_SCALE)),
            1.0
        );

        if (terminal == null) {
            terminal = new UltimateNetworkedTerminal(termWidth, termHeight, null, this::markChanged);
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

    @Override
    synchronized void reset() {
        if (terminal == null) return;
        terminal = null;
        markChanged();
    }

    @Override
    boolean pollResized() {
        return resized.getAndSet(false);
    }

    @Nullable
    @Override
    public UltimateNetworkedTerminal getTerminal() {
        return terminal;
    }
}
