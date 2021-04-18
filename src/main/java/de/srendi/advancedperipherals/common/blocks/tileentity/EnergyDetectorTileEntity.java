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

public class EnergyDetectorTileEntity extends PeripheralTileEntity<EnergyDetectorPeripheral> implements ITickableTileEntity {

    public int maxTransferRate = Integer.MAX_VALUE;
    public int transferRate = 0;

    public APEnergyStorage storage = new APEnergyStorage(0, 0);
    //creating an storage so cables will be connect.
    //The storage will not save any energy

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

    public void setMaxTransferRate(int rate) {
        maxTransferRate = rate;
    }

    @Override
    public void tick() {
        if (!world.isRemote) {
            sendOutPower();
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        compound.putInt("rateLimit", maxTransferRate);
        return compound;
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt) {
        maxTransferRate = nbt.getInt("rateLimit");
        super.read(state, nbt);
    }

    private void sendOutPower() {
        if (world.getTileEntity(pos.offset(energyInDirection)) == null || world.getTileEntity(pos.offset(energyOutDirection)) == null) {
            return;
        }
        TileEntity teIn = world.getTileEntity(pos.offset(energyInDirection));
        TileEntity teOut = world.getTileEntity(pos.offset(energyOutDirection));
        teIn.getCapability(CapabilityEnergy.ENERGY, energyInDirection.getOpposite()).map(handler ->
                teOut.getCapability(CapabilityEnergy.ENERGY, energyOutDirection.getOpposite()).map(handlerOut->{
                    if (handlerOut.canReceive()) {
                        int received = handlerOut.receiveEnergy(
                                Math.min(handler.getEnergyStored(), maxTransferRate), false);
                        transferRate = received;
                        handler.extractEnergy(received, false);
                        return received;
                    } else {
                        return true;
                    }
                }).orElse(true)).orElse(true);


    }
}
