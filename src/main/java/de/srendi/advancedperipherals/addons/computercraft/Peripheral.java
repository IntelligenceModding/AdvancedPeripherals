package de.srendi.advancedperipherals.addons.computercraft;

import dan200.computercraft.api.peripheral.IPeripheral;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Peripheral implements IPeripheral {

    String type;

    public Peripheral(String type) {
        this.type = type;
    }

    @NotNull
    @Override
    public String getType() {
        return type;
    }

    @Nullable
    @Override
    public Object getTarget() {
        return null;
    }

    @Override
    public boolean equals(@Nullable IPeripheral iPeripheral) {
        return false;
    }


}
