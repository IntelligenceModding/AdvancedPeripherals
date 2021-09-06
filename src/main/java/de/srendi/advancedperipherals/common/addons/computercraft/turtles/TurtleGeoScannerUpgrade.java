package de.srendi.advancedperipherals.common.addons.computercraft.turtles;

import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.lib.turtle.PeripheralTurtleUpgrade;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.GeoScannerPeripheral;
import de.srendi.advancedperipherals.common.setup.Blocks;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class TurtleGeoScannerUpgrade extends PeripheralTurtleUpgrade<GeoScannerPeripheral> {
    public static final ResourceLocation ID = new ResourceLocation(AdvancedPeripherals.MOD_ID, "geoscanner_turtle");

    private static final ModelResourceLocation leftModel = new ModelResourceLocation("advancedperipherals:turtle_geoscanner_upgrade_left", "inventory");
    private static final ModelResourceLocation rightModel = new ModelResourceLocation("advancedperipherals:turtle_geoscanner_upgrade_right", "inventory");

    public TurtleGeoScannerUpgrade() {
        super(ID, new ItemStack(Blocks.GEO_SCANNER.get()));
    }

    @Override
    protected ModelResourceLocation getLeftModel() {
        return leftModel;
    }

    @Override
    protected ModelResourceLocation getRightModel() {
        return rightModel;
    }

    @Override
    protected GeoScannerPeripheral buildPeripheral(@NotNull ITurtleAccess turtle, @NotNull TurtleSide side) {
        return new GeoScannerPeripheral(turtle, side);
    }
}
