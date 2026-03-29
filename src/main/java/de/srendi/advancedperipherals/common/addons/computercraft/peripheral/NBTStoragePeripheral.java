package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.LuaValues;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.shared.util.NBTUtil;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.BlockEntityPeripheralOwner;
import de.srendi.advancedperipherals.common.blocks.blockentities.NBTStorageEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class NBTStoragePeripheral extends BasePeripheral<BlockEntityPeripheralOwner<NBTStorageEntity>> {

    public static final String PERIPHERAL_TYPE = "nbt_storage";

    public NBTStoragePeripheral(NBTStorageEntity blockEntity) {
        super(PERIPHERAL_TYPE, new BlockEntityPeripheralOwner<>(blockEntity));
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
    public final MethodResult load() {
        return MethodResult.of(NBTUtil.toLua(owner.getBlockEntity().getStored()));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult save(IArguments args) throws LuaException {
        Object data = args.get(0);
        if (data == null) {
            throw new LuaException("argument #1 must provide a vaild SNBT string or a NBT-like table");
        }
        CompoundTag parsedData;
        if (data instanceof String snbt) {
            try {
                parsedData = TagParser.parseTag(snbt);
            } catch (CommandSyntaxException ex) {
                return MethodResult.of(false, String.format("Cannot parse SNBT: %s", ex.getMessage()));
            }
        } else if (data instanceof Map<?, ?> map) {
            parsedData = de.srendi.advancedperipherals.common.util.NBTUtil.mapToNBT(map);
        } else {
            throw LuaValues.badArgumentOf(args, 0, "string or table");
        }
        if (getNBTSize(parsedData) > APConfig.PERIPHERALS_CONFIG.nbtStorageMaxSize.get()) {
            return MethodResult.of(false, "NBT size is bigger than allowed");
        }
        owner.getBlockEntity().setStored(parsedData);
        return MethodResult.of(true);
    }

    private static int getNBTSize(CompoundTag data) {
        DataOutputStream dataOutput = new DataOutputStream(OutputStream.nullOutputStream());
        try {
            data.write(dataOutput);
        } catch (IOException e) {
            throw new AssertionError("Unexpected IOException", e);
        }
        return dataOutput.size();
    }
}
