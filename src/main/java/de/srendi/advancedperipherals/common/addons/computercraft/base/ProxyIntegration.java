package de.srendi.advancedperipherals.common.addons.computercraft.base;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ProxyIntegration implements IPeripheralProvider {

    @NotNull
    @Override
    public LazyOptional<IPeripheral> getPeripheral(@NotNull World world, @NotNull BlockPos blockPos, @NotNull Direction direction) {
        return null;
    }
}
