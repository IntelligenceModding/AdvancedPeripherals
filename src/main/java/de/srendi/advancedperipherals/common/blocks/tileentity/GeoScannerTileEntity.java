package de.srendi.advancedperipherals.common.blocks.tileentity;

import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.GeoScannerPeripheral;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralTileEntity;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import de.srendi.advancedperipherals.common.setup.TileEntityTypes;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.energy.EnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import net.minecraftforge.energy.CapabilityEnergy;

public class GeoScannerTileEntity extends PeripheralTileEntity<GeoScannerPeripheral> {

    private final LazyOptional<IEnergyStorage> lazyEnergyStorage = LazyOptional.of(() -> new EnergyStorage(AdvancedPeripheralsConfig.geoScannerMaxEnergyStorage));

    public GeoScannerTileEntity() {
        this(TileEntityTypes.GEO_SCANNER.get());
    }

    public GeoScannerTileEntity(final TileEntityType<?> tileEntityType) {
        super(tileEntityType);
    }
    
    @Override
    public <T1> LazyOptional<T1> getCapability(@NotNull Capability<T1> cap, @Nullable Direction direction) {
        if (cap == CapabilityEnergy.ENERGY) {
            return lazyEnergyStorage.cast();
        }
        return super.getCapability(cap, direction);
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        lazyEnergyStorage.resolve().ifPresent(iEnergyStorage -> {
            compound.putInt("energy", iEnergyStorage.getEnergyStored());
        });
        return super.save(compound);
    }

    @Override
    public void load(BlockState blockState, CompoundNBT compound) {
        super.load(blockState, compound);
        lazyEnergyStorage.resolve().ifPresent(iEnergyStorage -> {
            iEnergyStorage.receiveEnergy(compound.getInt("energy") - iEnergyStorage.getEnergyStored(), false);
        });
    }

    @Override
    protected void invalidateCaps() {
        super.invalidateCaps();
        this.lazyEnergyStorage.invalidate();
    }

    @Override
    protected GeoScannerPeripheral createPeripheral() {
        return new GeoScannerPeripheral("geoScanner", this);
    }
}