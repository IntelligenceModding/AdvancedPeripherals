package de.srendi.advancedperipherals.common.addons.computercraft.turtles;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.SaddlePeripheral;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.lib.turtle.PeripheralTurtleUpgrade;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class TurtleSaddleUpgrade extends PeripheralTurtleUpgrade<SaddlePeripheral> {

    public TurtleSaddleUpgrade(ResourceLocation id, ItemStack stack) {
        super(id, stack);
    }

    @Override
    public ModelResourceLocation getLeftModel() {
        return null;
    }

    @Override
    public ModelResourceLocation getRightModel() {
        return null;
    }

    @Override
    protected SaddlePeripheral buildPeripheral(@NotNull ITurtleAccess turtle, @NotNull TurtleSide side) {
        return new SaddlePeripheral(turtle, side);
    }

    @Override
    public void update(@NotNull ITurtleAccess turtle, @NotNull TurtleSide side) {
        super.update(turtle, side);
        if (APConfig.PERIPHERALS_CONFIG.enableSaddleTurtle.get()) {
            IPeripheral peripheral = turtle.getPeripheral(side);
            if (peripheral instanceof SaddlePeripheral saddlePeripheral) {
                saddlePeripheral.update();
            }
        }
    }
}
