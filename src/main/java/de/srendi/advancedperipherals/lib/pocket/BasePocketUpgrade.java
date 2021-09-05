package de.srendi.advancedperipherals.lib.pocket;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.pocket.AbstractPocketUpgrade;
import dan200.computercraft.api.pocket.IPocketAccess;
import de.srendi.advancedperipherals.lib.peripherals.IBasePeripheral;
import de.srendi.advancedperipherals.common.util.TranslationUtil;
import de.srendi.advancedperipherals.lib.peripherals.DisabledPeripheral;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public abstract class BasePocketUpgrade<T extends IBasePeripheral<?>> extends AbstractPocketUpgrade {

    protected T peripheral;

    protected BasePocketUpgrade(ResourceLocation id, String adjective, Supplier<? extends IItemProvider> stack) {
        super(id, adjective, stack);
    }

    protected BasePocketUpgrade(ResourceLocation id, Supplier<? extends IItemProvider> stack) {
        super(id, TranslationUtil.pocket(id.getPath()), stack);
    }

    protected abstract T getPeripheral(IPocketAccess access);

    @Nullable
    @Override
    public IPeripheral createPeripheral(@NotNull IPocketAccess access) {
        peripheral = getPeripheral(access);
        if (!peripheral.isEnabled())
            return DisabledPeripheral.INSTANCE;
        return peripheral;
    }
}
