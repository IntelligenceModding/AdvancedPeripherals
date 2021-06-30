package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.shared.util.NBTUtil;
import de.srendi.advancedperipherals.common.addons.computercraft.base.BasePeripheral;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralTileEntity;
import de.srendi.advancedperipherals.common.blocks.tileentity.NBTStorageTile;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;

public class NBTStoragePeripheral extends BasePeripheral {

    public NBTStoragePeripheral(String type, PeripheralTileEntity<?> tileEntity) {
        super(type, tileEntity);
    }

    @Override
    public boolean isEnabled() {
        return AdvancedPeripheralsConfig.enableNBTStorage;
    }

    @LuaFunction
    public final MethodResult read() {
        return MethodResult.of(NBTUtil.toLua(((NBTStorageTile) tileEntity).getStored()));
    }

    @LuaFunction
    public final MethodResult write(String dataInJson) {
        if (dataInJson.length() > AdvancedPeripheralsConfig.nbtStorageMaxSize) {
            return MethodResult.of(null, "JSON size is bigger than allowed");
        }
        CompoundNBT parsedData;
        try {
            parsedData = JsonToNBT.parseTag(dataInJson);
        } catch (CommandSyntaxException ex) {
            return MethodResult.of(null, String.format("Cannot parse json data to NBT: %s", ex.getMessage()));
        }
        ((NBTStorageTile) tileEntity).setStored(parsedData);
        return MethodResult.of(true);
    }
}
