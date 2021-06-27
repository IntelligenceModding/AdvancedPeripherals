package de.srendi.advancedperipherals.common.addons.computercraft.turtles;

import de.srendi.advancedperipherals.common.addons.computercraft.base.BaseTurtle;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.GeoScannerPeripheral;
import de.srendi.advancedperipherals.common.setup.Blocks;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class TurtleGeoScanner extends BaseTurtle<GeoScannerPeripheral> {

    private static final ModelResourceLocation leftModel = new ModelResourceLocation("advancedperipherals:turtle_geoscanner_upgrade_left", "inventory");
    private static final ModelResourceLocation rightModel = new ModelResourceLocation("advancedperipherals:turtle_geoscanner_upgrade_right", "inventory");

    public TurtleGeoScanner() {
        super("geoscanner_turtle", "turtle.advancedperipherals.geoscanner_turtle", new ItemStack(Blocks.GEO_SCANNER.get()));
    }

    @Override
    protected GeoScannerPeripheral createPeripheral() {
        return new GeoScannerPeripheral("geoScanner", (TileEntity) null);
    }

    @Override
    protected ModelResourceLocation getLeftModel() {
        return leftModel;
    }

    @Override
    protected ModelResourceLocation getRightModel() {
        return rightModel;
    }
}
