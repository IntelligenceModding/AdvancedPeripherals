package de.srendi.advancedperipherals.common.blocks.blockentities;

import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.GasDetectorPeripheral;
import de.srendi.advancedperipherals.common.addons.mekanism.MekanismCapabilities;
import de.srendi.advancedperipherals.common.blocks.base.BaseBlock;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralBlockEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.setup.APBlockEntityTypes;
import de.srendi.advancedperipherals.common.util.GasStorageProxy;
import de.srendi.advancedperipherals.common.util.ZeroGasTank;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class GasDetectorEntity extends PeripheralBlockEntity<GasDetectorPeripheral> {

    public int transferRate = 0;
    public GasStack lastFlowedGas = GasStack.EMPTY;

    //storageProxy that will forward the gas to the output but limit it to maxTransferRate
    public final GasStorageProxy storageProxy = new GasStorageProxy(this, APConfig.PERIPHERALS_CONFIG.gasDetectorMaxFlow.get());
    //a zero size, zero transfer gas storage to ensure that cables connect
    private final IGasHandler zeroStorage = new ZeroGasTank();
    private final LazyOptional<IGasHandler> gasStorageCap = LazyOptional.of(() -> storageProxy);
    private final LazyOptional<IGasHandler> zeroStorageCap = LazyOptional.of(() -> zeroStorage);
    private Optional<IGasHandler> outReceivingStorage = Optional.empty();

    private Direction gasInDirection = Direction.NORTH;
    private Direction gasOutDirection = Direction.SOUTH;

    public GasDetectorEntity(BlockPos pos, BlockState state) {
        super(APBlockEntityTypes.GAS_DETECTOR.get(), pos, state);
    }

    @NotNull
    @Override
    protected GasDetectorPeripheral createPeripheral() {
        return new GasDetectorPeripheral(this);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction direction) {
        gasInDirection = getBlockState().getValue(BaseBlock.ORIENTATION).front();
        gasOutDirection = getBlockState().getValue(BaseBlock.ORIENTATION).front().getOpposite();
        if (cap == MekanismCapabilities.GAS_HANDLER) {
            if (direction == gasInDirection) {
                return gasStorageCap.cast();
            } else if (direction == gasOutDirection) {
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
            // this handles the rare edge case that receiveGas is called multiple times in one tick
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
    public Optional<IGasHandler> getOutputStorage() {
        // the documentation says that the value of the LazyOptional should be cached locally and invalidated using addListener
        if (outReceivingStorage.isEmpty()) {
            BlockEntity teOut = level.getBlockEntity(worldPosition.relative(gasOutDirection));
            if (teOut == null) {
                return Optional.empty();
            }
            LazyOptional<IGasHandler> lazyOptionalOutStorage = teOut.getCapability(MekanismCapabilities.GAS_HANDLER, gasOutDirection.getOpposite());
            outReceivingStorage = lazyOptionalOutStorage.resolve();
            lazyOptionalOutStorage.addListener(l -> {
                outReceivingStorage = Optional.empty();
            });
        }
        return outReceivingStorage;
    }
}
