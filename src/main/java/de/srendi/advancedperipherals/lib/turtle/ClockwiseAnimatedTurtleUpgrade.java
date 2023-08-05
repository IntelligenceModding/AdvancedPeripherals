package de.srendi.advancedperipherals.lib.turtle;

import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.common.util.DataStorageUtil;
import de.srendi.advancedperipherals.lib.peripherals.IBasePeripheral;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class ClockwiseAnimatedTurtleUpgrade<T extends IBasePeripheral<?>> extends PeripheralTurtleUpgrade<T> {

    public static final String STORED_DATA_TAG = "storedData";

    protected ClockwiseAnimatedTurtleUpgrade(ResourceLocation id, ItemStack item) {
        super(id, item);
    }

    /*@NotNull
    @Override
    public TransformedModel getModel(@Nullable ITurtleAccess turtle, @NotNull TurtleSide side) {
        if (getLeftModel() == null) {
            PoseStack stack = new PoseStack();
            stack.pushPose();
            stack.translate(0.0f, 0.5f, 0.5f);
            if (turtle != null) {
                int rotationStep = DataStorageUtil.RotationCharge.get(turtle, side);
                stack.mulPose(Vector3f.XN.rotationDegrees(-10 * rotationStep));
            }
            stack.translate(0.0f, -0.5f, -0.5f);
            stack.mulPose(Vector3f.YN.rotationDegrees(90));
            if (side == TurtleSide.LEFT) {
                stack.translate(0, 0, -0.6);
            } else {
                stack.translate(0, 0, -1.4);
            }
            return TransformedModel.of(getCraftingItem(), new Transformation(stack.last().pose()));
        }
        return TransformedModel.of(side == TurtleSide.LEFT ? getLeftModel() : getRightModel());
    }*/

    // Optional callbacks for addons based on AP
    public void chargeConsumingCallback() {

    }

    @Override
    public ItemStack getUpgradeItem(CompoundTag upgradeData) {
        if (upgradeData.isEmpty()) return getCraftingItem();
        var baseItem = getCraftingItem().copy();
        baseItem.addTagElement(STORED_DATA_TAG, upgradeData);
        return baseItem;
    }

    @Override
    public CompoundTag getUpgradeData(ItemStack stack) {
        var storedData = stack.getTagElement(STORED_DATA_TAG);
        if (storedData == null)
            return new CompoundTag();
        return storedData;
    }

    @Override
    public boolean isItemSuitable(ItemStack stack) {
        if (stack.getTagElement(STORED_DATA_TAG) == null) return super.isItemSuitable(stack);
        var tweakedStack = stack.copy();
        tweakedStack.getOrCreateTag().remove(STORED_DATA_TAG);
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
}
