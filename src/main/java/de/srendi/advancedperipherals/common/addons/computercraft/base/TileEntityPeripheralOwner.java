package de.srendi.advancedperipherals.common.addons.computercraft.base;

import de.srendi.advancedperipherals.api.peripheral.IPeripheralOwner;
import de.srendi.advancedperipherals.api.peripheral.IPeripheralTileEntity;
import de.srendi.advancedperipherals.common.blocks.base.APTileEntityBlock;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralTileEntity;
import de.srendi.advancedperipherals.common.blocks.tileentity.InventoryManagerTile;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import de.srendi.advancedperipherals.common.util.fakeplayer.APFakePlayer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class TileEntityPeripheralOwner<T extends TileEntity & IPeripheralTileEntity> implements IPeripheralOwner {

    protected final T tileEntity;

    public TileEntityPeripheralOwner(T tileEntity) {
        this.tileEntity = tileEntity;
    }

    @Nullable
    @Override
    public String getCustomName() {
        return tileEntity.getTileData().getString("CustomName");
    }

    @Nullable
    @Override
    public World getWorld() {
        return tileEntity.getLevel();
    }

    @NotNull
    @Override
    public BlockPos getPos() {
        return tileEntity.getBlockPos();
    }

    @NotNull
    @Override
    public Direction getFacing() {
        return tileEntity.getBlockState().getValue(APTileEntityBlock.FACING);
    }

    @Nullable
    @Override
    public PlayerEntity getOwner() {
        if (tileEntity instanceof InventoryManagerTile)
            return ((InventoryManagerTile) tileEntity).getOwnerPlayer();
        return null;
    }

    @NotNull
    @Override
    public CompoundNBT getSettings() {
        return tileEntity.getApSettings();
    }

    @Override
    public int getFuelCount() {
        return tileEntity.getCapability(CapabilityEnergy.ENERGY).map(storage -> storage.getEnergyStored() / AdvancedPeripheralsConfig.energyToFuelRate).orElse(0);
    }

    @Override
    public int getFuelMaxCount() {
        return tileEntity.getCapability(CapabilityEnergy.ENERGY).map(storage -> storage.getMaxEnergyStored() / AdvancedPeripheralsConfig.energyToFuelRate).orElse(0);
    }

    @Override
    public boolean consumeFuel(int count, boolean simulate) {
        return tileEntity.getCapability(CapabilityEnergy.ENERGY).map(storage -> {
            int energyCount = count * AdvancedPeripheralsConfig.energyToFuelRate;
            int extractedCount = storage.extractEnergy(energyCount, true);
            if (extractedCount == energyCount) {
                if (simulate)
                    return true;
                storage.extractEnergy(energyCount, false);
                return true;
            }
            return false;
        }).orElse(false);
    }

    @Override
    public void addFuel(int count) {
        tileEntity.getCapability(CapabilityEnergy.ENERGY).ifPresent(storage -> {
            int energyCount = count * AdvancedPeripheralsConfig.energyToFuelRate;
            storage.receiveEnergy(energyCount, false);
        });
    }

    @Override
    public void triggerClientServerSync() {
        World world = tileEntity.getLevel();
        if (world != null) {
            tileEntity.setChanged();
            BlockPos pos = tileEntity.getBlockPos();
            BlockState state = tileEntity.getBlockState();
            world.sendBlockUpdated(pos, state, state, 3);
        }
    }

    @Override
    public <T1> T1 withPlayer(Function<APFakePlayer, T1> function) {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public ItemStack getToolInMainHand() {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack storeItem(ItemStack stored) {
        // TODO: tricks with capability needed
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public void destroyUpgrade() {
        World world = getWorld();
        if (world != null)
            getWorld().removeBlock(tileEntity.getBlockPos(), false);
    }

    @Override
    public boolean isMovementPossible(@NotNull World world, @NotNull BlockPos pos) {
        return false;
    }

    @Override
    public boolean move(@NotNull World world, @NotNull BlockPos pos) {
        return false;
    }

    @Override
    public Object getObjectInside() {
        return tileEntity;
    }
}
