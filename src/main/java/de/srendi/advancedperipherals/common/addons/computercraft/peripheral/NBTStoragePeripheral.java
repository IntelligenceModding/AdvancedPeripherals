package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.shared.util.NBTUtil;
import de.srendi.advancedperipherals.common.addons.computercraft.base.BasePeripheral;
import de.srendi.advancedperipherals.common.blocks.tileentity.NBTStorageTile;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import de.srendi.advancedperipherals.common.util.CountingWipingStream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

public class NBTStoragePeripheral extends BasePeripheral {

    private final NBTStorageTile tile;

    public NBTStoragePeripheral(String type, NBTStorageTile tileEntity) {
        super(type, tileEntity);
        this.tile = tileEntity;
    }

    @Override
    public boolean isEnabled() {
        return AdvancedPeripheralsConfig.enableNBTStorage;
    }

    @LuaFunction
    public final Map<String, Integer> getConfiguration() {
        Map<String, Integer> result = new HashMap<>();
        result.put("maxSize", AdvancedPeripheralsConfig.nbtStorageMaxSize);
        return result;
    }

    @LuaFunction
    public final MethodResult read() {
        return MethodResult.of(NBTUtil.toLua(tile.getStored()));
    }

    @LuaFunction
    public final MethodResult writeJson(String jsonData) {
        if (jsonData.length() > AdvancedPeripheralsConfig.nbtStorageMaxSize) {
            return MethodResult.of(null, "JSON size is bigger than allowed");
        }
        CompoundTag parsedData;
        try {
            parsedData = TagParser.parseTag(jsonData);
        } catch (CommandSyntaxException ex) {
            return MethodResult.of(null, String.format("Cannot parse json: %s", ex.getMessage()));
        }
        tile.setStored(parsedData);
        return MethodResult.of(true);
    }

    @LuaFunction
    public final MethodResult writeTable(Map<?, ?> data) {
        CountingWipingStream countingStream = new CountingWipingStream();
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(countingStream);
            objectOutputStream.writeObject(data);
            objectOutputStream.close();
        } catch (IOException e) {
            return MethodResult.of(null, String.format("No idea, how this happened, but java IO Exception appear %s", e.getMessage()));
        }
        if (countingStream.getWrittenBytes() > AdvancedPeripheralsConfig.nbtStorageMaxSize)
            return MethodResult.of(null, "JSON size is bigger than allowed");
        CompoundTag parsedData = (CompoundTag) de.srendi.advancedperipherals.common.util.NBTUtil.toDirectNBT(data);
        tile.setStored(parsedData);
        return MethodResult.of(true);
    }
}
