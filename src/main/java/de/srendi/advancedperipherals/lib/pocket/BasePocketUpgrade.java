package de.srendi.advancedperipherals.lib.pocket;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.pocket.AbstractPocketUpgrade;
import dan200.computercraft.api.pocket.IPocketAccess;
import de.srendi.advancedperipherals.common.setup.APDataComponents;
import de.srendi.advancedperipherals.common.util.DataComponentUtil;
import de.srendi.advancedperipherals.common.util.TranslationUtil;
import de.srendi.advancedperipherals.lib.peripherals.DisabledPeripheral;
import de.srendi.advancedperipherals.lib.peripherals.IBasePeripheral;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BasePocketUpgrade<T extends IBasePeripheral<?>> extends AbstractPocketUpgrade {
    protected BasePocketUpgrade(ResourceLocation id, ItemStack stack) {
        super(TranslationUtil.pocket(id.getPath()), stack);
    }

    protected abstract T buildPeripheral(IPocketAccess access);

    @Override
    public ItemStack getUpgradeItem(DataComponentPatch upgradeData) {
        return DataComponentUtil.patchStoredDataToItem(this.getCraftingItem(), upgradeData);
    }

    @Override
    public DataComponentPatch getUpgradeData(ItemStack stack) {
        return DataComponentUtil.getStoredDataFromItem(stack);
    }

    @Override
    public boolean isItemSuitable(@NotNull ItemStack stack) {
        if (stack.has(DataComponents.CUSTOM_NAME) || stack.has(APDataComponents.STORED_DATA)) {
            stack = stack.copy();
            stack.remove(DataComponents.CUSTOM_NAME);
            stack.remove(APDataComponents.STORED_DATA);
        }
        return super.isItemSuitable(stack);
    }

    @Nullable
    @Override
    public IPeripheral createPeripheral(@NotNull IPocketAccess access) {
        T peripheral = buildPeripheral(access);
        return peripheral.isEnabled() ? peripheral : new DisabledPeripheral(peripheral);
    }

    @Override
    public void update(@NotNull IPocketAccess access, @Nullable IPeripheral peripheral) {
        super.update(access, peripheral);
        if (peripheral instanceof IBasePeripheral<?> basePeripheral) {
            basePeripheral.update();
        }
    }
}
