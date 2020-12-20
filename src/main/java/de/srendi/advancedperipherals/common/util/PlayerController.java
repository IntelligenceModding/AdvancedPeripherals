package de.srendi.advancedperipherals.common.util;

import net.minecraft.world.IWorld;

public class PlayerController {

    private IWorld world;

    public void init(IWorld world) {
        this.world = world;
    }

    public IWorld getWorld() {
        return world;
    }
}
