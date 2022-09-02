package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.shared.util.NBTUtil;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.BlockEntityPeripheralOwner;
import de.srendi.advancedperipherals.common.blocks.base.APBlockEntityBlock;
import de.srendi.advancedperipherals.common.blocks.blockentities.BlockReaderEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockReaderPeripheral extends BasePeripheral<BlockEntityPeripheralOwner<BlockReaderEntity>> {

    public static final String TYPE = "blockReader";

    public BlockReaderPeripheral(BlockReaderEntity tileEntity) {
        super(TYPE, new BlockEntityPeripheralOwner<>(tileEntity));
    }

    @Override
    public boolean isEnabled() {
        return APConfig.PERIPHERALS_CONFIG.enableBlockReader.get();
    }

    @LuaFunction(mainThread = true)
    public final String getBlockName() {
        if (getBlockInFront().is(Blocks.AIR)) return "none";
        return ForgeRegistries.BLOCKS.getKey(getBlockInFront().getBlock()).toString();
    }

    @LuaFunction(mainThread = true)
    public final Object getBlockData() {
        if (getBlockInFront().is(Blocks.AIR) && !(getBlockInFront().getBlock() instanceof EntityBlock)) return null;
        BlockEntity target = getLevel().getBlockEntity(getPos().relative(getLevel().getBlockState(getPos()).getValue(APBlockEntityBlock.FACING)));
        return NBTUtil.toLua(target.saveWithoutMetadata());
    }

    private BlockState getBlockInFront() {
        return getLevel().getBlockState(getPos().relative(getLevel().getBlockState(getPos()).getValue(APBlockEntityBlock.FACING)));
    }
}
