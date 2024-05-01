package de.srendi.advancedperipherals.common.blocks.blockentities;

import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.FluidDetectorPeripheral;
import de.srendi.advancedperipherals.common.blocks.base.BaseBlock;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralBlockEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.setup.APBlockEntityTypes;
import de.srendi.advancedperipherals.common.util.FluidStorageProxy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class FluidDetectorEntity extends PeripheralBlockEntity<FluidDetectorPeripheral> {

    public int transferRate = 0;
    public FluidStack lastFlowedLiquid = FluidStack.EMPTY;

    //storageProxy that will forward the fluid to the output but limit it to maxTransferRate
    public final FluidStorageProxy storageProxy = new FluidStorageProxy(this, APConfig.PERIPHERALS_CONFIG.fluidDetectorMaxFlow.get());
    //a zero size, zero transfer fluid storage to ensure that cables connect
    private final FluidTank zeroStorage = new FluidTank(0);
    private final LazyOptional<IFluidHandler> fluidStorageCap = LazyOptional.of(() -> storageProxy);
    private final LazyOptional<IFluidHandler> zeroStorageCap = LazyOptional.of(() -> zeroStorage);
    private Optional<IFluidHandler> outReceivingStorage = Optional.empty();

    private Direction fluidInDetection = Direction.NORTH;
    private Direction fluidOutDirection = Direction.SOUTH;

    public FluidDetectorEntity(BlockPos pos, BlockState state) {
        super(APBlockEntityTypes.FLUID_DETECTOR.get(), pos, state);
    }

    @NotNull
    @Override
    protected FluidDetectorPeripheral createPeripheral() {
        return new FluidDetectorPeripheral(this);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction direction) {
        fluidInDetection = getBlockState().getValue(BaseBlock.ORIENTATION).front();
        fluidOutDirection = getBlockState().getValue(BaseBlock.ORIENTATION).front().getOpposite();
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            if (direction == fluidInDetection) {
                return fluidStorageCap.cast();
            } else if (direction == fluidOutDirection) {
                return zeroStorageCap.cast();
            }
        }
        return super.getCapability(cap, direction);
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag compound) {
        super.saveAdditional(compound);
        compound.putInt("rateLimit", storageProxy.getMaxTransferRate());
    }

    @Override
    public <T extends BlockEntity> void handleTick(Level level, BlockState state, BlockEntityType<T> type) {
        if (!level.isClientSide) {
            // this handles the rare edge case that receiveFluid is called multiple times in one tick
            transferRate = storageProxy.getTransferedInThisTick();
            storageProxy.resetTransferedInThisTick();
        }
    }

    @Override
    public void load(CompoundTag nbt) {
        storageProxy.setMaxTransferRate(nbt.getInt("rateLimit"));
        super.load(nbt);
    }

    // returns the cached output storage of the receiving block or fetches it if it has been invalidated
    @NotNull
    public Optional<IFluidHandler> getOutputStorage() {
        // the documentation says that the value of the LazyOptional should be cached locally and invalidated using addListener
        if (outReceivingStorage.isEmpty()) {
            BlockEntity teOut = level.getBlockEntity(worldPosition.relative(fluidOutDirection));
            if (teOut == null) {
                return Optional.empty();
            }
            LazyOptional<IFluidHandler> lazyOptionalOutStorage = teOut.getCapability(ForgeCapabilities.FLUID_HANDLER, fluidOutDirection.getOpposite());
            outReceivingStorage = lazyOptionalOutStorage.resolve();
            lazyOptionalOutStorage.addListener(l -> {
                outReceivingStorage = Optional.empty();
            });
        }
        return outReceivingStorage;
    }
}
