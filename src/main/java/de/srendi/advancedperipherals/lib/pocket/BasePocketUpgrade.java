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
        super(TranslationUtil.pocket(id.getPath()), stack);
    }

    protected abstract T getPeripheral(IPocketAccess access);

    @Nullable
    @Override
    public IPeripheral createPeripheral(@NotNull IPocketAccess access) {
        peripheral = getPeripheral(access);
        if (!peripheral.isEnabled()) {
            return new DisabledPeripheral(peripheral);
        }
        return peripheral;
    }

    @Override
    public void update(@NotNull IPocketAccess access, @Nullable IPeripheral peripheral) {
        super.update(access, peripheral);
        if (peripheral instanceof IBasePeripheral basePeripheral) {
            basePeripheral.update();
        }
    }

    @Override
    public boolean isItemSuitable(ItemStack stack) {
        return true;
    }
}
