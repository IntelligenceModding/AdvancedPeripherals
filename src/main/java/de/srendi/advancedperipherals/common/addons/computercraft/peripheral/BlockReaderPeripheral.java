package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.shared.util.NBTUtil;
import de.srendi.advancedperipherals.common.blocks.base.APTileEntityBlock;
import de.srendi.advancedperipherals.common.blocks.tileentity.BlockReaderTile;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import de.srendi.advancedperipherals.lib.peripherals.owner.TileEntityPeripheralOwner;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;

public class BlockReaderPeripheral extends BasePeripheral<TileEntityPeripheralOwner<BlockReaderTile>> {

    public static final String TYPE = "blockReader";

    public BlockReaderPeripheral(BlockReaderTile tileEntity) {
        super(TYPE, new TileEntityPeripheralOwner<>(tileEntity));
    }

    @Override
    public boolean isEnabled() {
        return AdvancedPeripheralsConfig.enableBlockReader;
    }

    @LuaFunction(mainThread = true)
    public final String getBlockName() {
        if (getBlockInFront().is(Blocks.AIR))
            return "none";
        return getBlockInFront().getBlock().getRegistryName().toString();
    }

    @LuaFunction(mainThread = true)
    public final Object getBlockData() {
        if (getBlockInFront().is(Blocks.AIR) && !getBlockInFront().getBlock().hasTileEntity(getBlockInFront()))
            return null;
        return NBTUtil.toLua(getWorld().getBlockEntity(getPos().relative(getWorld().getBlockState(getPos()).
                getValue(APTileEntityBlock.FACING))).save(new CompoundNBT()));
    }

    private BlockState getBlockInFront() {
        return getWorld().getBlockState(getPos().relative(getWorld().getBlockState(getPos()).getValue(APTileEntityBlock.FACING)));
    }
}
