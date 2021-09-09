package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.shared.util.NBTUtil;
import de.srendi.advancedperipherals.common.blocks.tileentity.NBTStorageTile;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import de.srendi.advancedperipherals.common.util.CountingWipingStream;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import de.srendi.advancedperipherals.lib.peripherals.owner.TileEntityPeripheralOwner;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;

public class NBTStoragePeripheral extends BasePeripheral<TileEntityPeripheralOwner<NBTStorageTile>> {

    public static final String TYPE = "nbtStorage";


    public NBTStoragePeripheral(NBTStorageTile tileEntity) {
        super(TYPE, new TileEntityPeripheralOwner<>(tileEntity));
    }

    @Override
    public boolean isEnabled() {
        return AdvancedPeripheralsConfig.enableNBTStorage;
    }

    @Override
    public Map<String, Object> getPeripheralConfiguration() {
        Map<String, Object> data = super.getPeripheralConfiguration();
        data.put("maxSize", AdvancedPeripheralsConfig.nbtStorageMaxSize);
        return data;
    }

    @LuaFunction(mainThread = true)
    public final MethodResult read() {
        return MethodResult.of(NBTUtil.toLua(owner.tileEntity.getStored()));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult writeJson(String jsonData) {
        if (jsonData.length() > AdvancedPeripheralsConfig.nbtStorageMaxSize) {
            return MethodResult.of(null, "JSON size is bigger than allowed");
        }
        CompoundNBT parsedData;
        try {
            parsedData = JsonToNBT.parseTag(jsonData);
        } catch (CommandSyntaxException ex) {
            return MethodResult.of(null, String.format("Cannot parse json: %s", ex.getMessage()));
        }
        owner.tileEntity.setStored(parsedData);
        return MethodResult.of(true);
    }

    @LuaFunction(mainThread = true)
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
        CompoundNBT parsedData = (CompoundNBT) de.srendi.advancedperipherals.common.util.NBTUtil.toDirectNBT(data);
        owner.tileEntity.setStored(parsedData);
        return MethodResult.of(true);
    }
}
