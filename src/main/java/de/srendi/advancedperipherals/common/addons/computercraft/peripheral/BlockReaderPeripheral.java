package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.shared.util.NBTUtil;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.BlockEntityPeripheralOwner;
import de.srendi.advancedperipherals.common.blocks.blockentities.BlockReaderEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

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
        if (getBlockInFront().is(Blocks.AIR)) return "none";
        return ForgeRegistries.BLOCKS.getKey(getBlockInFront().getBlock()).toString();
    }

    @LuaFunction(mainThread = true)
    public final Object getBlockData() {
        if (getBlockInFront().is(Blocks.AIR))
            return null;
        BlockEntity target = getLevel().getBlockEntity(getPos().relative(owner.getFacing()));
        if (target == null)
            return null;
        return NBTUtil.toLua(target.saveWithoutMetadata());
    }

    private BlockState getBlockInFront() {
        return getLevel().getBlockState(getPos().relative(owner.getFacing()));
    }
}
