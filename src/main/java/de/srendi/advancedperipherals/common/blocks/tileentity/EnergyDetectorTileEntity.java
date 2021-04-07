package de.srendi.advancedperipherals.common.blocks.tileentity;

import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.EnergyDetectorPeripheral;
import de.srendi.advancedperipherals.common.blocks.EnergyDetectorBlock;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralTileEntity;
import de.srendi.advancedperipherals.common.setup.TileEntityTypes;
import de.srendi.advancedperipherals.common.util.APEnergyStorage;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicInteger;

public class EnergyDetectorTileEntity extends PeripheralTileEntity<EnergyDetectorPeripheral> implements ITickableTileEntity {

    public int maxTransferRate = Integer.MAX_VALUE;
    public int transferRate = 0;

    public APEnergyStorage storageIn = new APEnergyStorage(Integer.MAX_VALUE, Integer.MAX_VALUE, 0);
    public APEnergyStorage storageOut = new APEnergyStorage(Integer.MAX_VALUE, 0, Integer.MAX_VALUE);

    LazyOptional<IEnergyStorage> energyStorageCapIn = LazyOptional.of(()->storageIn);
    LazyOptional<IEnergyStorage> energyStorageCapOut = LazyOptional.of(()->storageOut);

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
            if (direction == energyInDirection)
                return energyStorageCapIn.cast();
            if (direction == energyOutDirection) {
                return energyStorageCapOut.cast();
            }
        }
        return super.getCapability(cap, direction);
    }

    @Override
    public void tick() {
        if (!world.isRemote) {
            if (storageIn.getEnergyStored() > 0) {
                int received = storageOut.receivePower(storageIn.getEnergyStored(), false, true);
                storageIn.extractPower(received, false, true);
                this.transferRate = received;
            }
            if (storageOut.getEnergyStored() > 0) {
                sendOutPower();
            }
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        compound.putInt("rateLimit", maxTransferRate);
        compound.putInt("storageInEnergy", storageIn.getEnergyStored());
        compound.putInt("storageOutEnergy", storageOut.getEnergyStored());
        return compound;
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt) {
        maxTransferRate = nbt.getInt("rateLimit");
        storageIn.setEnergy(nbt.getInt("storageInEnergy"));
        storageOut.setEnergy(nbt.getInt("storageOutEnergy"));
        super.read(state, nbt);
    }

    private void sendOutPower() {
        AtomicInteger capacity = new AtomicInteger(storageOut.getEnergyStored());
        if (capacity.get() > 0) {
            Direction direction = energyOutDirection;
            TileEntity te = world.getTileEntity(pos.offset(direction));
            if (te != null) {
                te.getCapability(CapabilityEnergy.ENERGY, direction.getOpposite()).map(handler->{
                    if (handler.canReceive()) {
                        int received = handler.receiveEnergy(Math.min(capacity.get(), maxTransferRate), false);
                        capacity.addAndGet(-received);
                        storageOut.extractPower(received, false, false);
                        markDirty();
                        return capacity.get() > 0;
                    } else {
                        return true;
                    }
                }).orElse(true);
            }
        }
    }
}
