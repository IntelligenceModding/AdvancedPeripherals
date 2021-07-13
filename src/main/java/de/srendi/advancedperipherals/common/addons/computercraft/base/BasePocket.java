package de.srendi.advancedperipherals.common.addons.computercraft.base;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.pocket.AbstractPocketUpgrade;
import dan200.computercraft.api.pocket.IPocketAccess;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public abstract class BasePocket<T extends BasePeripheral> extends AbstractPocketUpgrade {

    protected T peripheral;

    protected BasePocket(ResourceLocation id, String adjective, Supplier<? extends IItemProvider> stack) {
        super(id, adjective, stack);
    }

    protected abstract T getPeripheral(IPocketAccess access);

    @Nullable
    @Override
    public IPeripheral createPeripheral(@NotNull IPocketAccess iPocketAccess) {
        peripheral = getPeripheral(iPocketAccess);
        if (!peripheral.isEnabled())
            return DisabledPeripheral.INSTANCE;
        return peripheral;
    }
}
