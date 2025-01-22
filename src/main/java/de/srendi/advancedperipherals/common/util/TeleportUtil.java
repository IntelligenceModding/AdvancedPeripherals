package de.srendi.advancedperipherals.common.util;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public final class TeleportUtil {
    private TeleportUtil() {}

    public static <T extends Entity> T teleportToWithPassengers(T entity, ServerLevel newLevel, Vec3 newPos) {
        Vec3 oldPos = entity.position();
        List<Entity> passengers = new ArrayList<>(entity.getPassengers());
        T newEntity;
        if (entity instanceof ServerPlayer player) {
            // TODO <1.20.1>: player will be reconstruct in 1.20.1
            player.teleportTo(newLevel, newPos.x, newPos.y, newPos.z, player.getYRot(), player.getXRot());
            newEntity = entity;
        } else {
            newEntity = (T) entity.getType().create(newLevel);
            if (newEntity == null) {
                return null;
            }
            entity.ejectPassengers();
            newEntity.restoreFrom(entity);
            newEntity.moveTo(newPos.x, newPos.y, newPos.z, newEntity.getYRot(), newEntity.getXRot());
            newLevel.addDuringTeleport(newEntity);
            entity.setRemoved(Entity.RemovalReason.CHANGED_DIMENSION);
        }
        for (Entity p : passengers) {
            Entity newPassenger = teleportToWithPassengers(p, newLevel, p.position().subtract(oldPos).add(newPos));
            if (newPassenger != null) {
                newPassenger.startRiding(newEntity, true);
            }
        }
        return newEntity;
    }
}
