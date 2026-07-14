package de.srendi.advancedperipherals.common.addons.ae2.disk;

import appeng.api.storage.cells.ICellHandler;
import appeng.api.storage.cells.ISaveProvider;
import appeng.api.storage.cells.StorageCell;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class AEDiskHandler implements ICellHandler {
    public static final AEDiskHandler INSTANCE = new AEDiskHandler();

    private AEDiskHandler() {}

    @Override
    public boolean isCell(ItemStack stack) {
        return stack.getItem() instanceof AEDiskCell;
    }

    @Override
    @Nullable
    public StorageCell getCellInventory(ItemStack stack, @Nullable ISaveProvider host) {
        if (!(stack.getItem() instanceof AEDiskCell cell)) {
            return null;
        }
        if (!cell.isEnabled()) {
            return null;
        }
        return new AEDiskCellStorage(stack, cell, host);
    }
}
