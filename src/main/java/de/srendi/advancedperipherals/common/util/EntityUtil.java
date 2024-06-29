package de.srendi.advancedperipherals.common.util;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.UUID;

public class EntityUtil {
    /**
     * Searches all levels for an {@link Entity} with the given {@link UUID}.
     * @param uuid the unique identifier of the {@code Entity}
     * @return an {@code Entity} that has the given {@code UUID} if one is found
     * @throws IllegalArgumentException throws if there is no {@code Entity} with the given {@code UUID}
     */
    public static Entity getEntityFromUUID(UUID uuid) {
        for (ServerLevel level : ServerLifecycleHooks.getCurrentServer().getAllLevels()) {
            Entity entity = level.getEntity(uuid);
            if (entity != null)
                return entity;
        }
        throw new IllegalArgumentException("No Entity with UUID '" + uuid + "' exists in any level");
    }
}
