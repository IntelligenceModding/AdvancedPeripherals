package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.shared.util.NBTUtil;
import de.srendi.advancedperipherals.common.addons.computercraft.base.BasePeripheral;
import de.srendi.advancedperipherals.common.blocks.base.APTileEntityBlock;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralTileEntity;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;

public class BlockReaderPeripheral extends BasePeripheral {

    public BlockReaderPeripheral(String type, PeripheralTileEntity<?> tileEntity) {
        super(type, tileEntity);
    }

    @Override
    public boolean isEnabled() {
        return AdvancedPeripheralsConfig.enableBlockReader;
    }

    @LuaFunction
    public final String getBlockName() {
        if (getBlockInFront().is(Blocks.AIR))
            return "none";
        return getBlockInFront().getBlock().getRegistryName().toString();
    }

    @LuaFunction(mainThread = true)
    public final Object getBlockData() {
        if (getBlockInFront().is(Blocks.AIR) && !(getBlockInFront().getBlock() instanceof EntityBlock))
            return null;
        return NBTUtil.toLua(getLevel().getBlockEntity(getPos().relative(getLevel().getBlockState(getPos()).
                getValue(APTileEntityBlock.FACING))).save(new CompoundTag()));
    }

    private BlockState getBlockInFront() {
        return getLevel().getBlockState(getPos().relative(getLevel().getBlockState(getPos()).getValue(APTileEntityBlock.FACING)));
    }
}
