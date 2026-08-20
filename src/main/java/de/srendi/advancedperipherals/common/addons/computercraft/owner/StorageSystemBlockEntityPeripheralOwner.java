package de.srendi.advancedperipherals.common.addons.computercraft.owner;

import de.srendi.advancedperipherals.common.util.inventory.FluidFilter;
import de.srendi.advancedperipherals.common.util.inventory.IStorageSystemFluidHandler;
import de.srendi.advancedperipherals.common.util.inventory.IStorageSystemItemHandler;
import de.srendi.advancedperipherals.common.util.inventory.ItemFilter;
import de.srendi.advancedperipherals.common.util.inventory.StorageProcessor;
import de.srendi.advancedperipherals.lib.peripherals.IPeripheralBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public abstract class StorageSystemBlockEntityPeripheralOwner<T extends BlockEntity & IPeripheralBlockEntity>
    extends BlockEntityPeripheralOwner<T>
    implements IStorageSystemPeripheralOwner, IStorageSystemItemHandler, IStorageSystemFluidHandler {

    public StorageSystemBlockEntityPeripheralOwner(@NotNull T blockEntity) {
        super(blockEntity);
    }

    @NotNull
    public abstract IStorageSystemItemHandler getStorageSystemItemHandler();

    @NotNull
    public abstract IStorageSystemFluidHandler getStorageSystemFluidHandler();

    @Override
    public ItemStack insertItem(ItemStack stack, boolean simulate) {
        return this.getStorageSystemItemHandler().insertItem(stack, simulate);
    }

    @Override
    public int extractItems(ItemFilter filter, StorageProcessor<ItemStack> processor, boolean simulate) {
        return this.getStorageSystemItemHandler().extractItems(filter, processor, simulate);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        return this.getStorageSystemFluidHandler().fill(resource, action);
    }

    @Override
    public int extractFluids(FluidFilter filter, StorageProcessor<FluidStack> processor, FluidAction action) {
        return this.getStorageSystemFluidHandler().extractFluids(filter, processor, action);
    }

}
