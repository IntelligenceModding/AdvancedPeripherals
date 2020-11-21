package de.srendi.advancedperipherals.blocks;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import de.srendi.advancedperipherals.addons.computercraft.AdvancedPeripheral;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

public class LightSensor extends Block implements IPeripheralProvider{

    public LightSensor() {
        super(Properties.create(Material.IRON)
                .hardnessAndResistance(1, 5)
                .harvestLevel(0)
                .sound(SoundType.METAL)
                .harvestTool(ToolType.AXE)
                .setRequiresTool());
    }

    @NotNull
    @Override
    public LazyOptional<IPeripheral> getPeripheral(@NotNull World world, @NotNull BlockPos blockPos, @NotNull Direction direction) {
        BlockState block = world.getBlockState(blockPos);
        return block.getBlock() instanceof LightSensor
                ? LazyOptional.of(() -> new AdvancedPeripheral("lightSensor"))
                : LazyOptional.empty();
    }
}
