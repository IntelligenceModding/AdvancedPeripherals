package de.srendi.advancedperipherals.common.util;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class SideHelper {

    public static boolean isClientPlayer(LivingEntity player) {
        return player.level.isClientSide && player instanceof Player && player.level.getServer() == null
                && player == Minecraft.getInstance().player;
    }
}
