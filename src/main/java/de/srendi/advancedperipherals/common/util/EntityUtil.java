package de.srendi.advancedperipherals.common.util;

import dan200.computercraft.api.lua.LuaException;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.Map;
import java.util.UUID;

public class EntityUtil {
	/**
	 * A version of {@link EntityUtil#getEntityFromUUID} designed to be called from a
	 * {@link dan200.computercraft.api.lua.LuaFunction LuaFunction}.
	 * @param uuidList list containing 4 integers that together make a {@code UUID}
	 * @return an {@code Entity} that has the given {@code UUID} if one is found
	 * @throws LuaException throws if {@code uuidList} is incorrectly formatted or no {@code Entity} exists with the
	 * given {@code UUID}
	 */
	public static Entity getEntityFromUUIDLua(Map<?, ?> uuidList) throws LuaException {
		try {
			UUID uuid = LuaConverter.luaToUUID(uuidList);
			return getEntityFromUUID(uuid);
		} catch (IllegalArgumentException e) {
			throw new LuaException(e.getMessage());
		}
	}

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
