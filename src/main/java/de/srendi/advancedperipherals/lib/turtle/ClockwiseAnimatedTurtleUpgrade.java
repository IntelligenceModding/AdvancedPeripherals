package de.srendi.advancedperipherals.lib.turtle;

import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.common.util.DataStorageUtil;
import de.srendi.advancedperipherals.lib.peripherals.IBasePeripheral;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Set;

import static de.srendi.advancedperipherals.common.setup.DataComponents.ABILITY_COOLDOWN;
import static de.srendi.advancedperipherals.common.setup.DataComponents.TURTLE_UPGRADE_STORED_DATA;
import static de.srendi.advancedperipherals.common.setup.DataComponents.ROTATION_CHARGE_SETTING;

public abstract class ClockwiseAnimatedTurtleUpgrade<T extends IBasePeripheral<?>> extends PeripheralTurtleUpgrade<T> {


    protected ClockwiseAnimatedTurtleUpgrade(ResourceLocation id, ItemStack item) {
        super(id, item);
    }

    // Optional callbacks for addons based on AP
    public void chargeConsumingCallback() {

    }

    @Override
    public ItemStack getUpgradeItem(DataComponentPatch upgradeData) {
        if (upgradeData.isEmpty())
            return getCraftingItem();
        ItemStack baseItem = getCraftingItem().copy();
        baseItem.applyComponents(upgradeData);
        return baseItem;
    }

    @Override
    public DataComponentPatch getUpgradeData(ItemStack stack) {
        var storedData = stack.get(TURTLE_UPGRADE_STORED_DATA);
        if (storedData == null)
            return DataComponentPatch.EMPTY;
        return storedData;
    }

    @Override
    public boolean isItemSuitable(@NotNull ItemStack stack) {
        if (!stack.has(TURTLE_UPGRADE_STORED_DATA) && !stack.has(ABILITY_COOLDOWN) && !stack.has(ROTATION_CHARGE_SETTING))
            return super.isItemSuitable(stack);
        ItemStack tweakedStack = stack.copy();

        // Ignore custom display names
        tweakedStack.remove(DataComponents.CUSTOM_NAME);
        // We can safely try to remove either of them even if one of them is missing.
        tweakedStack.remove(ROTATION_CHARGE_SETTING);
        tweakedStack.remove(TURTLE_UPGRADE_STORED_DATA);
        tweakedStack.remove(ABILITY_COOLDOWN);

        dataTypesToIgnore().forEach(tweakedStack::remove);
        return super.isItemSuitable(tweakedStack);
    }

    @Override
    public void update(@NotNull ITurtleAccess turtle, @NotNull TurtleSide side) {
        super.update(turtle, side);
        if (tick % 2 == 0) {
            if (DataStorageUtil.RotationCharge.consume(turtle, side))
                chargeConsumingCallback();
        }
    }

    /**
     * Data Components to ignore for validating the item in the turtle - prevents that a used turtle upgrade can't be inserted again like an end automata
     * @return a set of data types to ignore
     */
    public Set<DataComponentType<?>> dataTypesToIgnore() {
        return Collections.emptySet();
    }
}
