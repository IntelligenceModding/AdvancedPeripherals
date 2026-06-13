package de.srendi.advancedperipherals.common.addons.computercraft.luaapi;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaAPI;
import dan200.computercraft.api.lua.ILuaFunction;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.APAddon;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

public final class APLuaAPI implements ILuaAPI {
    public static final APLuaAPI INSTANCE = new APLuaAPI();

    private static final String NAME = AdvancedPeripherals.MOD_ID;

    @Override
    public String[] getNames() {
        return new String[]{NAME};
    }

    @LuaFunction
    public Map<String, String> getAddonVersions() {
        ModList modList = ModList.get();
        Map<String, String> modVersions = new HashMap<>();

        for (String modId : APAddon.getAllModIds()) {
            Optional<? extends ModContainer> addon = modList.getModContainerById(modId);
            addon.ifPresent(modContainer -> modVersions.put(modId, modContainer.getModInfo().getVersion().toString()));
        }

        return modVersions;
    }

    @LuaFunction
    public String getMCVersion() throws LuaException {
        Optional<? extends ModContainer> minecraftContainer = ModList.get().getModContainerById("minecraft");

        return minecraftContainer.map(modContainer -> modContainer.getModInfo().getVersion().toString())
                .orElseThrow(() -> new LuaException("minecraft is not installed??? report this!!!"));

    }

    @LuaFunction
    public String getAPVersion() throws LuaException {
        Optional<? extends ModContainer> minecraftContainer = ModList.get().getModContainerById(AdvancedPeripherals.MOD_ID);

        return minecraftContainer.map(modContainer -> modContainer.getModInfo().getVersion().toString())
                .orElseThrow(() -> new LuaException(AdvancedPeripherals.MOD_ID + " is not installed??? report this!!!"));
    }

    @LuaFunction
    public ILuaFunction iterPlayerStatKeys() {
        return new StatTypeIterator();
    }

    @SuppressWarnings("rawtypes")
    private static final class StatTypeIterator implements ILuaFunction {
        private final Iterator<StatType<?>> typeIter;
        private StatType currentStatType = null;
        private Iterator<Stat> currentStatIter = null;

        StatTypeIterator() {
            this.typeIter = BuiltInRegistries.STAT_TYPE.iterator();
        }

        @Override
        public MethodResult call(IArguments args) throws LuaException {
            if (this.currentStatIter != null && !this.currentStatIter.hasNext()) {
                this.currentStatType = null;
                this.currentStatIter = null;
            }
            while (this.currentStatIter == null) {
                if (!this.typeIter.hasNext()) {
                    return MethodResult.of();
                }
                this.currentStatType = this.typeIter.next();
                this.currentStatIter = currentStatType.iterator();
                if (!this.currentStatIter.hasNext()) {
                    this.currentStatType = null;
                    this.currentStatIter = null;
                }
            }
            return MethodResult.of(this.currentStatIter.next().getName());
        }
    }
}
