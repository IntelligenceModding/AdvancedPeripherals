/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2022. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */
package de.srendi.advancedperipherals.common.addons.computercraft.peripheral.monitor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

final class UltimateMonitorState
{
    public static final UltimateMonitorState UNLOADED = new UltimateMonitorState( State.UNLOADED, null );
    public static final UltimateMonitorState MISSING = new UltimateMonitorState( State.MISSING, null );

    private final State state;
    private final UltimateMonitorEntity monitor;

    private UltimateMonitorState( @Nonnull State state, @Nullable UltimateMonitorEntity monitor )
    {
        this.state = state;
        this.monitor = monitor;
    }

    public static UltimateMonitorState present( @Nonnull UltimateMonitorEntity monitor )
    {
        return new UltimateMonitorState( State.PRESENT, monitor );
    }

    public boolean isPresent()
    {
        return state == State.PRESENT;
    }

    public boolean isMissing()
    {
        return state == State.MISSING;
    }

    @Nullable
    public UltimateMonitorEntity getMonitor()
    {
        return monitor;
    }

    enum State
    {
        UNLOADED,
        MISSING,
        PRESENT,
    }
}
