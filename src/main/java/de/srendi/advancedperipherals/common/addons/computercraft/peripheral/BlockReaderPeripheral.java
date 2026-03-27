package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.shared.util.NBTUtil;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.BlockEntityPeripheralOwner;
import de.srendi.advancedperipherals.common.blocks.blockentities.BlockReaderEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

public class BlockReaderPeripheral extends BasePeripheral<BlockEntityPeripheralOwner<BlockReaderEntity>> {

    public static final String PERIPHERAL_TYPE = "block_reader";

    public BlockReaderPeripheral(BlockReaderEntity tileEntity) {
        super(PERIPHERAL_TYPE, new BlockEntityPeripheralOwner<>(tileEntity));
    }

    @Override
    public boolean isEnabled() {
        return APConfig.PERIPHERALS_CONFIG.enableBlockReader.get();
    }

    @LuaFunction(mainThread = true)
    public final String getBlockName() {
        if (getTargetBlock().isAir()) {
            return null;
        }
        return BuiltInRegistries.BLOCK.getKey(getTargetBlock().getBlock()).toString();
    }

    @LuaFunction(mainThread = true)
    public final Map<String, Object> getBlockState() {
        BlockState state = getTargetBlock();
        return state.isAir() ? null : LuaConverter.blockStateToLua(state);
    }

    @LuaFunction(mainThread = true)
    public final Map<String, Object> getBlockData() {
        if (getTargetBlock().isAir()) {
            return null;
        }
        BlockEntity target = getLevel().getBlockEntity(getTargetBlockPos());
        if (target == null) {
            return null;
        }
        return (Map<String, Object>) NBTUtil.toLua(target.saveWithId(getLevel().registryAccess()));
    }

    @LuaFunction(mainThread = true)
    public final boolean hasBlockEntity() {
        if (getTargetBlock().isAir()) {
            return false;
        }
        return getLevel().getBlockEntity(getTargetBlockPos()) != null;
    }

    protected BlockPos getTargetBlockPos() {
        return getPos().relative(owner.getFacing());
    }

    private BlockState getTargetBlock() {
        return getLevel().getBlockState(getTargetBlockPos());
    }
}
