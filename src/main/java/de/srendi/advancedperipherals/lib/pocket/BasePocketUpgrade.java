package de.srendi.advancedperipherals.lib.pocket;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.pocket.AbstractPocketUpgrade;
import dan200.computercraft.api.pocket.IPocketAccess;
import de.srendi.advancedperipherals.common.setup.APDataComponents;
import de.srendi.advancedperipherals.common.util.TranslationUtil;
import de.srendi.advancedperipherals.lib.peripherals.DisabledPeripheral;
import de.srendi.advancedperipherals.lib.peripherals.IBasePeripheral;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BasePocketUpgrade<T extends IBasePeripheral<?>> extends AbstractPocketUpgrade {
    protected BasePocketUpgrade(ResourceLocation id, ItemStack stack) {
        super(id, TranslationUtil.pocket(id.getPath()), stack);
    }

    @NotNull
    protected abstract T buildPeripheral(@NotNull IPocketAccess access);

    @Override
    public ItemStack getUpgradeItem(CompoundTag upgradeData) {
        ItemStack stack = this.getCraftingItem().copy();
        CompoundTag data = upgradeData.getCompound(APDataComponents.STORED_DATA);
        stack.getOrCreateTag().put("BlockEntityTag", data);
        Component name = Component.Serializer.fromJson(data.getString("CustomName"));
        if (name != null) {
            stack.setHoverName(name);
        }
        return stack;
    }

    @Override
    public CompoundTag getUpgradeData(ItemStack stack) {
        CompoundTag data = stack.getTagElement("BlockEntityTag");
        if (data == null) {
            data = new CompoundTag();
        }
        if (stack.hasCustomHoverName()) {
            data.putString("CustomName", Component.Serializer.toJson(stack.getHoverName()));
        }
        CompoundTag wrapped = new CompoundTag();
        wrapped.put(APDataComponents.STORED_DATA, data);
        return wrapped;
    }

    @Override
    public boolean isItemSuitable(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && (tag.contains("display") || tag.contains("RepairCost") || tag.contains("BlockEntityTag"))) {
            stack = stack.copy();
            stack.removeTagKey("display");
            stack.removeTagKey("RepairCost");
            stack.removeTagKey("BlockEntityTag");
        }
        return super.isItemSuitable(stack);
    }

    @Override
    @Nullable
    public IPeripheral createPeripheral(IPocketAccess access) {
        T peripheral = buildPeripheral(access);
        return peripheral.isEnabled() ? peripheral : new DisabledPeripheral(peripheral);
    }

    @Override
    public void update(IPocketAccess access, @Nullable IPeripheral peripheral) {
        super.update(access, peripheral);
        if (peripheral instanceof IBasePeripheral<?> basePeripheral) {
            basePeripheral.update();
        }
    }
}
