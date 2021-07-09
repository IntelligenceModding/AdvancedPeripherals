package de.srendi.advancedperipherals.common.addons.computercraft.base;

import com.mojang.blaze3d.matrix.MatrixStack;
import dan200.computercraft.api.client.TransformedModel;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraft.util.math.vector.Vector3f;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ModelTransformingTurtle<T extends MechanicSoulPeripheral> extends BaseTurtle<T> {

    public ModelTransformingTurtle(String id, String adjective, ItemStack item) {
        super(id, adjective, item);
    }

    @NotNull
    @Override
    public TransformedModel getModel(@Nullable ITurtleAccess iTurtleAccess, @NotNull TurtleSide turtleSide) {
        if (getLeftModel() == null) {
            MatrixStack stack = new MatrixStack();
            stack.pushPose();
            stack.translate(0.0f, 0.5f, 0.5f);
            int sign = 1;
            if (turtleSide == TurtleSide.LEFT)
                sign = -1;
            if (peripheral != null)
                stack.mulPose(Vector3f.XN.rotationDegrees(10 * peripheral.getRotationStep() * sign));
            stack.translate(0.0f, -0.5f, -0.5f);
            stack.mulPose(Vector3f.YN.rotationDegrees(90));
            stack.translate(0, 0, -0.6);
            return TransformedModel.of(getCraftingItem(), new TransformationMatrix(stack.last().pose()));
        }
        return TransformedModel.of(turtleSide == TurtleSide.LEFT ? getLeftModel() : getRightModel());
    }

    @Override
    public void update(@NotNull ITurtleAccess turtle, @NotNull TurtleSide side) {
        super.update(turtle, side);
        if (peripheral != null && tick %3 == 0)
            peripheral.consumeRotationCharge();
    }
}
