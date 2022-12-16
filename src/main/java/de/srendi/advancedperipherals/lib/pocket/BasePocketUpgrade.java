package de.srendi.advancedperipherals.lib.pocket;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.pocket.AbstractPocketUpgrade;
import dan200.computercraft.api.pocket.IPocketAccess;
import de.srendi.advancedperipherals.common.util.TranslationUtil;
import de.srendi.advancedperipherals.lib.peripherals.DisabledPeripheral;
import de.srendi.advancedperipherals.lib.peripherals.IBasePeripheral;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BasePocketUpgrade<T extends IBasePeripheral<?>> extends AbstractPocketUpgrade {

    protected T peripheral;

    protected BasePocketUpgrade(ResourceLocation id, ItemStack stack) {
        super(id, TranslationUtil.pocket(id.getPath()), stack);
    }

    protected abstract T getPeripheral(IPocketAccess access);

    @Nullable
    @Override
    public IPeripheral createPeripheral(@NotNull IPocketAccess access) {
        peripheral = getPeripheral(access);
        if (!peripheral.isEnabled()) return DisabledPeripheral.INSTANCE;
        return peripheral;
    }
}
