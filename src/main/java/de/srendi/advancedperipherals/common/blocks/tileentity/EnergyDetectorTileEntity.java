package de.srendi.advancedperipherals.common.blocks.tileentity;

import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.EnergyDetectorPeripheral;
import de.srendi.advancedperipherals.common.blocks.EnergyDetectorBlock;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralTileEntity;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import de.srendi.advancedperipherals.common.setup.TileEntityTypes;
import de.srendi.advancedperipherals.common.util.EnergyStorageProxy;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EnergyDetectorTileEntity extends PeripheralTileEntity<EnergyDetectorPeripheral> implements ITickableTileEntity {

    public int transferRate = 0;
    @NotNull
    private Optional<IEnergyStorage> outStorage = Optional.empty();

    // creating an storageProxy that will forward the energy to the output but limit it to maxTransferRate
    public EnergyStorageProxy storage = new EnergyStorageProxy(this, AdvancedPeripheralsConfig.energyDetectorMaxFlow);

    LazyOptional<IEnergyStorage> energyStorageCap = LazyOptional.of(()->storage);

    Direction energyInDirection = Direction.NORTH;
    Direction energyOutDirection = Direction.SOUTH;

    public EnergyDetectorTileEntity() {
        super(TileEntityTypes.ENERGY_DETECTOR.get());
    }

    @Override
    protected EnergyDetectorPeripheral createPeripheral() {
        return new EnergyDetectorPeripheral("energyDetector", this);
    }

    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction direction) {
        energyInDirection = getBlockState().get(EnergyDetectorBlock.FACING);
        energyOutDirection = getBlockState().get(EnergyDetectorBlock.FACING).getOpposite();
        if (cap == CapabilityEnergy.ENERGY) {
            if (direction == energyInDirection || direction == energyOutDirection)
                return energyStorageCap.cast();
        }
        return super.getCapability(cap, direction);
    }

    @Override
    public void tick() {
        if (!world.isRemote) {
            // this handles the rare edge case that receiveEnergy is called multiple times in one tick
            transferRate = storage.getTransferedInThisTick();
            storage.resetTransferedInThisTick();
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        compound.putInt("rateLimit", storage.getMaxTransferRate());
        return compound;
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt) {
        storage.setMaxTransferRate( nbt.getInt("rateLimit"));
        super.read(state, nbt);
    }

    /*
    @Nullable
    public LazyOptional<IEnergyStorage> getOutputStorage() {
        TileEntity teOut = world.getTileEntity(pos.offset(energyOutDirection));
        if (teOut == null) {
            return null;
        }

        return teOut.getCapability(CapabilityEnergy.ENERGY, energyOutDirection.getOpposite());
    */
    // the documentation says that the value of the LazyOptional should be cached locally and invallidated using addListener
    // this alternative code shoud do this only need to add an private property outStorage to the class
    @NotNull
    public Optional<IEnergyStorage> getOutputStorage() {
        if (outStorage.isEmpty()) {
            TileEntity teOut = world.getTileEntity(pos.offset(energyOutDirection));
            if (teOut == null) {
                return Optional.empty();
            }
            LazyOptional<IEnergyStorage> lazyOptionalOutStorage = teOut.getCapability(CapabilityEnergy.ENERGY, energyOutDirection.getOpposite());
            outStorage = lazyOptionalOutStorage.resolve();
            lazyOptionalOutStorage.addListener(l -> {
                outStorage = Optional.empty();
            });
        }
        return outStorage;
    }
}
