package de.srendi.advancedperipherals.common.addons.computercraft.luaapi;

import dan200.computercraft.api.lua.ILuaAPI;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.APAddon;
import de.srendi.advancedperipherals.common.util.StatusConstants;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class GlobalVersionsLuaAPI implements ILuaAPI {
    public static final String INSTANCE = new GlobalVersionsLuaAPI();

    private static final String NAME = AdvancedPeripherals.MOD_ID;

    @Override
    public String[] getNames() {
        return new String[]{NAME};
    }

    @LuaFunction
    public final Object getAddonVersions() {
        Map<String, String> modVersions = new HashMap<>();

        for (String modId : APAddon.getAllModIds()) {
            Optional<? extends ModContainer> addon = ModList.get().getModContainerById(modId);
            addon.ifPresent(modContainer -> modVersions.put(modId, modContainer.getModInfo().getVersion().toString()));
        }

        return modVersions;
    }

    @LuaFunction
    public final MethodResult getMCVersion() {
        Optional<? extends ModContainer> minecraftContainer = ModList.get().getModContainerById("minecraft");

        return minecraftContainer.map(modContainer -> MethodResult.of(modContainer.getModInfo().getVersion().toString()))
                .orElse(MethodResult.of(null, StatusConstants.UNKNOWN_ERROR));

    }

    @LuaFunction
    public final MethodResult getAPVersion() {
        Optional<? extends ModContainer> minecraftContainer = ModList.get().getModContainerById(AdvancedPeripherals.MOD_ID);

        return minecraftContainer.map(modContainer -> MethodResult.of(modContainer.getModInfo().getVersion().toString()))
                .orElse(MethodResult.of(null, StatusConstants.UNKNOWN_ERROR));
    }
}
