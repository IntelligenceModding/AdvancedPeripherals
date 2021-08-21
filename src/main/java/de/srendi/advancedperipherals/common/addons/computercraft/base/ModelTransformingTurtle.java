package de.srendi.advancedperipherals.common.addons.computercraft.base;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;
import dan200.computercraft.api.client.TransformedModel;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.common.addons.computercraft.operations.AutomataCorePeripheral;
import de.srendi.advancedperipherals.common.util.DataStorageUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ModelTransformingTurtle<T extends AutomataCorePeripheral> extends BaseTurtle<T> {

    public ModelTransformingTurtle(ResourceLocation id, String adjective, ItemStack item) {
        super(id, adjective, item);
    }

    public ModelTransformingTurtle(ResourceLocation id, ItemStack item) {
        super(id, item);
    }

    @NotNull
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
    }

    // Optional callbacks for addons based on AP
    public void chargeConsumingCallback() {

    }

    @Override
    public void update(@NotNull ITurtleAccess turtle, @NotNull TurtleSide side) {
        super.update(turtle, side);
        if (tick % 2 == 0)
            if (DataStorageUtil.RotationCharge.consume(turtle, side))
                chargeConsumingCallback();
    }
}
