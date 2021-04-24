package de.srendi.advancedperipherals.common.util;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public class SideHelper {
    public static boolean isClientPlayer(LivingEntity player) {
        return player.world.isRemote && player instanceof PlayerEntity && player.world.getServer() == null
                && player == Minecraft.getInstance().player;
    }
}
