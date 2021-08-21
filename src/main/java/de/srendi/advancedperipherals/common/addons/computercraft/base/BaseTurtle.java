package de.srendi.advancedperipherals.common.addons.computercraft.base;

import dan200.computercraft.api.client.TransformedModel;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.AbstractTurtleUpgrade;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.api.turtle.TurtleUpgradeType;
import de.srendi.advancedperipherals.common.util.TranslationUtil;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.TransformationMatrix;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BaseTurtle<T extends BasePeripheral> extends AbstractTurtleUpgrade {
    protected int tick;

    public BaseTurtle(ResourceLocation id, String adjective, ItemStack item) {
        super(id, TurtleUpgradeType.PERIPHERAL, adjective, item);
    }

    public BaseTurtle(ResourceLocation id, ItemStack item) {
        super(id, TurtleUpgradeType.PERIPHERAL, TranslationUtil.turtle(id.getPath()), item);
    }

    protected abstract ModelResourceLocation getLeftModel();

    protected abstract ModelResourceLocation getRightModel();

    protected abstract T buildPeripheral(@NotNull ITurtleAccess turtle, @NotNull TurtleSide side);

    @NotNull
    @Override
    public TransformedModel getModel(@Nullable ITurtleAccess iTurtleAccess, @NotNull TurtleSide turtleSide) {
        if (getLeftModel() == null) {
            float xOffset = turtleSide == TurtleSide.LEFT ? -0.40625f : 0.40625f;
            Matrix4f transform = new Matrix4f(new float[]{
                    0.0f, 0.0f, -1.0f, 1.0f + xOffset,
                    1.0f, 0.0f, 0.0f, 0.0f,
                    0.0f, -1.0f, 0.0f, 1.0f,
                    0.0f, 0.0f, 0.0f, 1.0f,
            });
            return TransformedModel.of(getCraftingItem(), new TransformationMatrix(transform));
        }
        return TransformedModel.of(turtleSide == TurtleSide.LEFT ? getLeftModel() : getRightModel());
    }

    @Nullable
    @Override
    public IPeripheral createPeripheral(@NotNull ITurtleAccess turtle, @NotNull TurtleSide side) {
        T peripheral = buildPeripheral(turtle, side);
        if (!peripheral.isEnabled()) {
            return DisabledPeripheral.INSTANCE;
        }
        return peripheral;
    }
}