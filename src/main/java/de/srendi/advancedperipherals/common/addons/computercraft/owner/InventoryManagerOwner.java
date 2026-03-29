package de.srendi.advancedperipherals.common.addons.computercraft.owner;

import de.srendi.advancedperipherals.common.blocks.blockentities.InventoryManagerEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class InventoryManagerOwner extends BlockEntityPeripheralOwner<InventoryManagerEntity> {
    public InventoryManagerOwner(InventoryManagerEntity tile) {
        super(tile);
    }

    @Override
    @Nullable
    public Player getOwner() {
        return getBlockEntity().getOwnerPlayer();
    }
}
