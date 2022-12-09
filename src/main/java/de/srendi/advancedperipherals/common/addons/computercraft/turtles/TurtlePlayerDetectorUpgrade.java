package de.srendi.advancedperipherals.common.addons.computercraft.turtles;

import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.PlayerDetectorPeripheral;
import de.srendi.advancedperipherals.lib.turtle.PeripheralTurtleUpgrade;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class TurtlePlayerDetectorUpgrade extends PeripheralTurtleUpgrade<PlayerDetectorPeripheral> {

    public TurtlePlayerDetectorUpgrade(ResourceLocation id, ItemStack stack) {
        super(id, stack);
    }

    @Override
    public ModelResourceLocation getLeftModel() {
        return new ModelResourceLocation(AdvancedPeripherals.getRL("turtle_player_upgrade_left"), "inventory");
    }

    @Override
    public ModelResourceLocation getRightModel() {
        return new ModelResourceLocation(AdvancedPeripherals.getRL("turtle_player_upgrade_right"), "inventory");
    }

    @Override
    protected PlayerDetectorPeripheral buildPeripheral(@NotNull ITurtleAccess turtle, @NotNull TurtleSide side) {
        return new PlayerDetectorPeripheral(turtle, side);
    }
}
