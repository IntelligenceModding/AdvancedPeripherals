package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.shared.util.NBTUtil;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.BlockEntityPeripheralOwner;
import de.srendi.advancedperipherals.common.blocks.blockentities.NBTStorageEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.util.CountingWipingStream;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;

public class NBTStoragePeripheral extends BasePeripheral<BlockEntityPeripheralOwner<NBTStorageEntity>> {

    public static final String PERIPHERAL_TYPE = "nbt_storage";

    public NBTStoragePeripheral(NBTStorageEntity tileEntity) {
        super(PERIPHERAL_TYPE, new BlockEntityPeripheralOwner<>(tileEntity));
    }

    @Override
    public boolean isEnabled() {
        return APConfig.PERIPHERALS_CONFIG.enableNBTStorage.get();
    }

    @Override
    public Map<String, Object> getPeripheralConfiguration() {
        Map<String, Object> data = super.getPeripheralConfiguration();
        data.put("maxSize", APConfig.PERIPHERALS_CONFIG.nbtStorageMaxSize.get());
        return data;
    }

    @LuaFunction(mainThread = true)
    public final MethodResult read() {
        return MethodResult.of(NBTUtil.toLua(owner.tileEntity.getStored()));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult writeJson(String jsonData) {
        if (jsonData.length() > APConfig.PERIPHERALS_CONFIG.nbtStorageMaxSize.get()) {
            return MethodResult.of(null, "JSON size is bigger than allowed");
        }
        CompoundTag parsedData;
        try {
            parsedData = TagParser.parseTag(jsonData);
        } catch (CommandSyntaxException ex) {
            return MethodResult.of(null, String.format("Cannot parse json: %s", ex.getMessage()));
        }
        owner.tileEntity.setStored(parsedData);
        return MethodResult.of(true);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult writeTable(Map<?, ?> data) {
        CountingWipingStream countingStream = new CountingWipingStream();
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(countingStream)) {
            objectOutputStream.writeObject(data);
        } catch (IOException e) {
            return MethodResult.of(null, String.format("No idea, how this happened, but java IO Exception appear %s", e.getMessage()));
        }
        if (countingStream.getWrittenBytes() > APConfig.PERIPHERALS_CONFIG.nbtStorageMaxSize.get())
            return MethodResult.of(null, "JSON size is bigger than allowed");
        CompoundTag parsedData = (CompoundTag) de.srendi.advancedperipherals.common.util.NBTUtil.toDirectNBT(data);
        owner.tileEntity.setStored(parsedData);
        return MethodResult.of(true);
    }
}
