package de.srendi.advancedperipherals.common.addons.computercraft.base;

import de.srendi.advancedperipherals.common.blocks.base.APTileEntityBlock;
import de.srendi.advancedperipherals.common.blocks.tileentity.InventoryManagerTile;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import de.srendi.advancedperipherals.common.util.DataStorageUtil;
import de.srendi.advancedperipherals.common.util.fakeplayer.APFakePlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.energy.CapabilityEnergy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class TileEntityPeripheralOwner<T extends BlockEntity & IPeripheralTileEntity> implements IPeripheralOwner {

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
    public Level getLevel() {
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
    public Player getOwner() {
        if (tileEntity instanceof InventoryManagerTile)
            return ((InventoryManagerTile) tileEntity).getOwnerPlayer();
        return null;
    }

    @NotNull
    @Override
    public CompoundTag getDataStorage() {
        return DataStorageUtil.getDataStorage(tileEntity);
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
        Level level = getLevel();
        if (level != null)
            getLevel().removeBlock(tileEntity.getBlockPos(), false);
    }

    @Override
    public boolean isMovementPossible(@NotNull Level level, @NotNull BlockPos pos) {
        return false;
    }

    @Override
    public boolean move(@NotNull Level level, @NotNull BlockPos pos) {
        return false;
    }
}
