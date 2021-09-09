package de.srendi.advancedperipherals.common.addons.storagedrawers;

import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawersStandard;
import de.srendi.advancedperipherals.common.addons.computercraft.integrations.IntegrationPeripheralProvider;
import de.srendi.advancedperipherals.common.util.InventoryUtil;

public class Integration implements Runnable {
    @Override
    public void run() {
        IntegrationPeripheralProvider.registerTileEntityIntegration(DrawerIntegration::new, TileEntityDrawers.class);

        InventoryUtil.registerExtractor(TileEntityDrawersStandard.class::isInstance, obj -> new DrawerItemHandler(((TileEntityDrawersStandard) obj).getGroup()));
    }
}
