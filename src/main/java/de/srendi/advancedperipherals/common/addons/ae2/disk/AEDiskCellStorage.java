package de.srendi.advancedperipherals.common.addons.ae2.disk;

import appeng.api.stacks.KeyCounter;
import appeng.api.storage.cells.CellState;
import appeng.api.storage.cells.ISaveProvider;
import appeng.api.storage.cells.StorageCell;
import dan200.computercraft.api.filesystem.Mount;
import dan200.computercraft.api.filesystem.WritableMount;
import dan200.computercraft.shared.util.NonNegativeId;
import de.srendi.advancedperipherals.common.setup.APDataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Objects;

public class AEDiskCellStorage implements StorageCell {
    protected final ItemStack stack;
    protected final AEDiskCell cell;
    @Nullable
    protected final ISaveProvider host;
    protected final NonNegativeId diskId;
    protected final AEDiskKey aeKey;
    protected final Mount mount;
    private int refreshCD = 0;

    public AEDiskCellStorage(ItemStack stack, AEDiskCell cell, @Nullable ISaveProvider host) {
        this.stack = stack;
        this.cell = cell;
        this.host = host;
        boolean needInit = !stack.has(APDataComponents.DISK_ID);
        this.mount = cell.getMedia().createDataMount(stack, ServerLifecycleHooks.getCurrentServer().overworld());
        this.diskId = Objects.requireNonNull(stack.get(APDataComponents.DISK_ID), "AEDiskCell media must assign a diskId");
        this.aeKey = AEDiskKey.of(this.diskId, this.mount);
        if (needInit && host != null) {
            host.saveChanges();
        }
    }

    @Nullable
    public NonNegativeId getDiskId() {
        return this.diskId;
    }

    public Mount getMount() {
        return this.mount;
    }

    @Override
    public Component getDescription() {
        return this.stack.getHoverName();
    }

    @Override
    public void getAvailableStacks(KeyCounter out) {
        if (!(mount instanceof WritableMount writableMount)) {
            return;
        }
        long remaining;
        try {
            remaining = writableMount.getRemainingSpace();
        } catch (IOException e) {
            remaining = 0;
        }
        long usedBytes = writableMount.getCapacity() - remaining;
        out.add(this.aeKey, usedBytes);

        this.refreshCD--;
        if (this.refreshCD <= 0) {
            this.refreshCD = 61;
            // DISK_USED_BYTES only used for tooltip rendering. CC: T itself manage the disk capacity.
            this.stack.set(APDataComponents.DISK_USED_BYTES, usedBytes);
            if (this.host != null) {
                this.host.saveChanges();
            }
        }
    }

    @Override
    public CellState getStatus() {
        if (this.getDiskId() == null) {
            return CellState.EMPTY;
        }
        if (mount instanceof WritableMount writableMount) {
            long remaining;
            try {
                remaining = writableMount.getRemainingSpace();
            } catch (IOException e) {
                remaining = 0;
            }
            if (remaining > 0) {
                return CellState.TYPES_FULL;
            }
        }
        return CellState.FULL;
    }

    @Override
    public double getIdleDrain() {
        return this.cell.getIdleDrain();
    }

    @Override
    public void persist() {
        this.stack.set(APDataComponents.DISK_ID, this.diskId);
    }
}
