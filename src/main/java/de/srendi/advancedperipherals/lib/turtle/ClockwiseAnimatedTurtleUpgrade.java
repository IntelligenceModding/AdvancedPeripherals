/*
 *     Copyright 2024 Intelligence Modding @ https://intelligence-modding.de
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.srendi.advancedperipherals.lib.turtle;

import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.common.util.DataStorageUtil;
import de.srendi.advancedperipherals.lib.peripherals.IBasePeripheral;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class ClockwiseAnimatedTurtleUpgrade<T extends IBasePeripheral<?>> extends PeripheralTurtleUpgrade<T> {

    protected ClockwiseAnimatedTurtleUpgrade(ResourceLocation id, ItemStack item) {
        super(id, item);
    }

    /*
     * @NotNull
     * 
     * @Override public TransformedModel getModel(@Nullable ITurtleAccess
     * turtle, @NotNull TurtleSide side) { if (getLeftModel() == null) { PoseStack
     * stack = new PoseStack(); stack.pushPose(); stack.translate(0.0f, 0.5f, 0.5f);
     * if (turtle != null) { int rotationStep =
     * DataStorageUtil.RotationCharge.get(turtle, side);
     * stack.mulPose(Vector3f.XN.rotationDegrees(-10 * rotationStep)); }
     * stack.translate(0.0f, -0.5f, -0.5f);
     * stack.mulPose(Vector3f.YN.rotationDegrees(90)); if (side == TurtleSide.LEFT)
     * { stack.translate(0, 0, -0.6); } else { stack.translate(0, 0, -1.4); } return
     * TransformedModel.of(getCraftingItem(), new
     * Transformation(stack.last().pose())); } return TransformedModel.of(side ==
     * TurtleSide.LEFT ? getLeftModel() : getRightModel()); }
     */

    // Optional callbacks for addons based on AP
    public void chargeConsumingCallback() {

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
