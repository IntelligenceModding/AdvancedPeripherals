package de.srendi.advancedperipherals.common.addons.computercraft.apis;

import dan200.computercraft.api.lua.*;
import dan200.computercraft.shared.util.NBTUtil;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.util.EntityUtil;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class EntityAPI implements ILuaAPI {

	private static EntityAPI entityAPI;

	@Override
	public String[] getNames() {
		return new String[]{"entity"};
	}

	public static EntityAPI create(@NotNull IComputerSystem computer) {
		if (entityAPI == null && APConfig.API_CONFIG.enableEntityAPI.get())
			entityAPI = new EntityAPI();
		return entityAPI;
	}

	@LuaFunction(mainThread = true)
	public final Object getNBT(IArguments arguments) throws LuaException {
		if (!APConfig.API_CONFIG.enableGetNBT.get())
			throw new LuaException("This function is disabled in the config. Activate it or ask an admin if they can activate it.");
		Entity entity = EntityUtil.getEntityFromUUIDLua(arguments.getTable(0));
		return NBTUtil.toLua(entity.serializeNBT());
	}

	@LuaFunction(mainThread = true)
	public final Object getName(IArguments arguments) throws LuaException {
		Entity entity = EntityUtil.getEntityFromUUIDLua(arguments.getTable(0));
		return entity.getName().getString();
	}

	@LuaFunction(mainThread = true)
	public final Map<String, Object> getBoundingBox(IArguments arguments) throws LuaException {
		if (!APConfig.API_CONFIG.enableGetBoundingBox.get())
			throw new LuaException("This function is disabled in the config. Activate it or ask an admin if they can activate it.");
		Entity entity = EntityUtil.getEntityFromUUIDLua(arguments.getTable(0));
		return LuaConverter.aabbToObject(entity.getBoundingBox());
	}

	@LuaFunction(mainThread = true)
	public final Map<String, Object> getPos(IArguments arguments) throws LuaException {
		if (!APConfig.API_CONFIG.enableGetPos.get())
			throw new LuaException("This function is disabled in the config. Activate it or ask an admin if they can activate it.");
		Entity entity = EntityUtil.getEntityFromUUIDLua(arguments.getTable(0));
		return LuaConverter.vec3ToLua(entity.getPosition(1));
	}

	/**
	 * Returns entity data depending on the class of the entity. Included data is either not included in the entities
	 * NBT data, or is especially useful so is included for convenience. Because most of this data
	 * doesn't overlap with NBT, it ends up mainly being values that exist only at runtime.
	 */
	@LuaFunction(mainThread = true)
	public final Map<String, Object> getData(IArguments arguments) throws LuaException {
		if (!APConfig.API_CONFIG.enableGetData.get())
			throw new LuaException("This function is disabled in the config. Activate it or ask an admin if they can activate it.");
		Entity entity = EntityUtil.getEntityFromUUIDLua(arguments.getTable(0));
		return LuaConverter.completeEntityToLua(entity);
	}

	/**
	 * Returns persistent data added to entities by mods.
	 */
	@LuaFunction(mainThread = true)
	public final Object getPersistentData(IArguments arguments) throws LuaException {
		if (!APConfig.API_CONFIG.enableGetPersistentData.get())
			throw new LuaException("This function is disabled in the config. Activate it or ask an admin if they can activate it.");
		Entity entity = EntityUtil.getEntityFromUUIDLua(arguments.getTable(0));
		return NBTUtil.toLua(entity.getPersistentData());
	}
}
