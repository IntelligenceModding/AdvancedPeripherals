package de.srendi.advancedperipherals.common.util;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public class SideHelper {
    public static boolean isClientPlayer(LivingEntity player) {
        return player.level.isClientSide && player instanceof PlayerEntity && player.level.getServer() == null
                && player == Minecraft.getInstance().player;
    }
}
