package de.srendi.advancedperipherals.common.addons.computercraft.apis;

import dan200.computercraft.api.lua.*;
import dan200.computercraft.shared.util.NBTUtil;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.util.EntityUtil;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class EntityAPI {

    public static final EntityAPI ENTITY_API = new EntityAPI();

    @LuaFunction(mainThread = true)
    public final Object getNBT(IArguments arguments) throws LuaException {
        if (!APConfig.API_CONFIG.enableGetNBT.get())
            throw new LuaException("This function is disabled in the config. Activate it or ask an admin if they can activate it.");
        Entity entity = EntityUtil.getEntityFromUUID(UUID.fromString(arguments.getString(0)));
        if (!APConfig.API_CONFIG.enablePlayerAccess.get() && entity instanceof Player)
            return MethodResult.of(null, "Using players in EntityAPI is disabled in the config. Activate it or ask an admin if they can activate it.");
        return NBTUtil.toLua(entity.serializeNBT());
    }

    @LuaFunction(mainThread = true)
    public final Object getName(IArguments arguments) throws LuaException {
        Entity entity = EntityUtil.getEntityFromUUID(UUID.fromString(arguments.getString(0)));
        return entity.getName().getString();
    }

    @LuaFunction(mainThread = true)
    public final Object getBoundingBox(IArguments arguments) throws LuaException {
        if (!APConfig.API_CONFIG.enableGetBoundingBox.get())
            throw new LuaException("This function is disabled in the config. Activate it or ask an admin if they can activate it.");
        Entity entity = EntityUtil.getEntityFromUUID(UUID.fromString(arguments.getString(0)));
        if (!APConfig.API_CONFIG.enablePlayerAccess.get() && entity instanceof Player)
            return MethodResult.of(null, "Using players in EntityAPI is disabled in the config. Activate it or ask an admin if they can activate it.");
        return LuaConverter.aabbToObject(entity.getBoundingBox());
    }

    @LuaFunction(mainThread = true)
    public final Object getPos(IArguments arguments) throws LuaException {
        if (!APConfig.API_CONFIG.enableGetPos.get())
            throw new LuaException("This function is disabled in the config. Activate it or ask an admin if they can activate it.");
        Entity entity = EntityUtil.getEntityFromUUID(UUID.fromString(arguments.getString(0)));
        if (!APConfig.API_CONFIG.enablePlayerAccess.get() && entity instanceof Player)
            return MethodResult.of(null, "Using players in EntityAPI is disabled in the config. Activate it or ask an admin if they can activate it.");
        return LuaConverter.vec3ToLua(entity.position());
    }

    /**
     * Returns entity data depending on the class of the entity. Included data is either not included in the entities
     * NBT data, or is especially useful so is included for convenience. Because most of this data
     * doesn't overlap with NBT, it ends up mainly being values that exist only at runtime.
     */
    @LuaFunction(mainThread = true)
    public final Object getData(IArguments arguments) throws LuaException {
        if (!APConfig.API_CONFIG.enableGetData.get())
            throw new LuaException("This function is disabled in the config. Activate it or ask an admin if they can activate it.");
        Entity entity = EntityUtil.getEntityFromUUID(UUID.fromString(arguments.getString(0)));
        boolean detailed = arguments.count() > 1 && arguments.getBoolean(1);
        if (!APConfig.API_CONFIG.enablePlayerAccess.get() && entity instanceof Player)
            return MethodResult.of(null, "Using players in EntityAPI is disabled in the config. Activate it or ask an admin if they can activate it.");
        return LuaConverter.completeEntityToLua(entity, detailed);
    }

    /**
     * Returns persistent data added to entities by mods.
     */
    @LuaFunction(mainThread = true)
    public final Object getPersistentData(IArguments arguments) throws LuaException {
        if (!APConfig.API_CONFIG.enableGetPersistentData.get())
            throw new LuaException("This function is disabled in the config. Activate it or ask an admin if they can activate it.");
        Entity entity = EntityUtil.getEntityFromUUID(UUID.fromString(arguments.getString(0)));
        if (!APConfig.API_CONFIG.enablePlayerAccess.get() && entity instanceof Player)
            return MethodResult.of(null, "Using players in EntityAPI is disabled in the config. Activate it or ask an admin if they can activate it.");
        return NBTUtil.toLua(entity.getPersistentData());
    }
}
