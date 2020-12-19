package de.srendi.advancedperipherals.common.addons.computercraft;

import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AdvancedPeripheralProvider implements ICapabilityProvider {

    private final IPeripheral impl;
    private final LazyOptional<IPeripheral> lazy;

    AdvancedPeripheralProvider(ILuaMethodProvider luaMethodProvider) {
        impl = new AdvancedPeripheral(luaMethodProvider);
        lazy = LazyOptional.of(() -> impl);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return ComputerCraft.PERIPHERAL_CAPABILITY.orEmpty(cap, lazy);
    }
}
