package de.srendi.advancedperipherals.common.blocks.base;

import net.minecraft.network.chat.Component;
import net.minecraft.world.Nameable;

public interface VarNameable extends Nameable {
    void setName(Component name);
}
