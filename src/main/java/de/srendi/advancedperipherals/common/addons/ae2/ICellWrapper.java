package de.srendi.advancedperipherals.common.addons.ae2;

import appeng.api.stacks.AEKeyType;
import appeng.api.storage.cells.IBasicCellItem;
import appeng.me.cells.BasicCellHandler;
import appeng.me.cells.BasicCellInventory;
import de.srendi.advancedperipherals.common.addons.APAddon;
import de.srendi.advancedperipherals.common.addons.ae2.disk.AEDiskCellItem;
import de.srendi.advancedperipherals.common.addons.ae2.disk.AEDiskCellStorage;
import de.srendi.advancedperipherals.common.addons.ae2.disk.AEDiskHandler;
import de.srendi.advancedperipherals.common.addons.ae2.disk.AEDiskKeys;
import io.github.projectet.ae2things.storage.DISKCellHandler;
import io.github.projectet.ae2things.storage.DISKCellInventory;
import io.github.projectet.ae2things.storage.IDISKCellItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public interface ICellWrapper {
    Item item();
    AEKeyType keyType();
    long maxBytes();
    long usedBytes();
    long bytesPerType();
    long maxTypes();

    default long freeBytes() {
        return this.maxBytes() - this.usedBytes();
    }

    static ICellWrapper of(Item item, ItemStack stack) {
        if (item instanceof IBasicCellItem cell) {
            return new BasicCellWrapper(cell, stack);
        }
        if (APAddon.AE2THINGS.isLoaded() && item instanceof IDISKCellItem cell) {
            return new AE2ThingsDiskCellWrapper(cell, stack);
        }
        if (item instanceof AEDiskCellItem cell) {
            return new APDiskCellWrapper(cell, stack);
        }
        return null;
    }
}

@SuppressWarnings("checkstyle:OneTopLevelClass")
final class BasicCellWrapper implements ICellWrapper {
    final IBasicCellItem cell;
    final ItemStack stack;
    final BasicCellInventory inv;

    BasicCellWrapper(IBasicCellItem cell, ItemStack stack) {
        this.cell = cell;
        this.stack = stack;
        this.inv = BasicCellHandler.INSTANCE.getCellInventory(stack, null);
    }

    @Override
    public Item item() {
        return (Item) this.cell;
    }

    @Override
    public AEKeyType keyType() {
        return this.cell.getKeyType();
    }

    @Override
    public long maxBytes() {
        return this.cell.getBytes(this.stack);
    }

    @Override
    public long usedBytes() {
        return this.inv == null ? 0 : this.inv.getUsedBytes();
    }

    @Override
    public long bytesPerType() {
        return this.cell.getBytesPerType(this.stack);
    }

    @Override
    public long maxTypes() {
        return this.cell.getTotalTypes(this.stack);
    }
}

@SuppressWarnings("checkstyle:OneTopLevelClass")
final class AE2ThingsDiskCellWrapper implements ICellWrapper {
    final IDISKCellItem cell;
    final ItemStack stack;
    final DISKCellInventory inv;

    AE2ThingsDiskCellWrapper(IDISKCellItem cell, ItemStack stack) {
        this.cell = cell;
        this.stack = stack;
        this.inv = DISKCellHandler.INSTANCE.getCellInventory(stack, null);
    }

    @Override
    public Item item() {
        return (Item) this.cell;
    }

    @Override
    public AEKeyType keyType() {
        return this.cell.getKeyType();
    }

    @Override
    public long maxBytes() {
        return this.cell.getBytes(this.stack);
    }

    @Override
    public long usedBytes() {
        return this.inv == null ? 0 : this.inv.getStoredItemCount();
    }

    @Override
    public long bytesPerType() {
        return 0;
    }

    @Override
    public long maxTypes() {
        return Long.MAX_VALUE;
    }
}

@SuppressWarnings("checkstyle:OneTopLevelClass")
final class APDiskCellWrapper implements ICellWrapper {
    final AEDiskCellItem cell;
    final ItemStack stack;
    final AEDiskCellStorage inv;

    APDiskCellWrapper(AEDiskCellItem cell, ItemStack stack) {
        this.cell = cell;
        this.stack = stack;
        this.inv = AEDiskHandler.INSTANCE.getCellInventory(stack, null);
    }

    @Override
    public Item item() {
        return this.cell;
    }

    @Override
    public AEKeyType keyType() {
        return AEDiskKeys.INSTANCE;
    }

    @Override
    public long maxBytes() {
        return this.cell.getMaxBytes();
    }

    @Override
    public long usedBytes() {
        return this.inv == null ? 0 : this.inv.getUsedBytes();
    }

    @Override
    public long bytesPerType() {
        return 0;
    }

    @Override
    public long maxTypes() {
        return 1;
    }
}
