package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.BlockEntityPeripheralOwner;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.IPeripheralOwner;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.TurtlePeripheralOwner;
import de.srendi.advancedperipherals.common.blocks.blockentities.BlockReaderEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import de.srendi.advancedperipherals.common.util.LuaOps;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

public class BlockReaderPeripheral extends BasePeripheral<IPeripheralOwner> {

    public static final String PERIPHERAL_TYPE = "block_reader";

    protected BlockReaderPeripheral(IPeripheralOwner owner) {
        super(PERIPHERAL_TYPE, owner);
    }

    public BlockReaderPeripheral(BlockReaderEntity tileEntity) {
        this(new BlockEntityPeripheralOwner<>(tileEntity));
    }

    public BlockReaderPeripheral(ITurtleAccess turtle, TurtleSide side) {
        this(new TurtlePeripheralOwner(turtle, side));
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
        return (Map<String, Object>) CompoundTag.CODEC
            .encodeStart(LuaOps.INSTANCE, target.saveWithId())
            .result()
            .get();
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
