package de.srendi.advancedperipherals.common.blocks.base;

import de.srendi.advancedperipherals.common.util.proxy.IStorageProxy;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @param <T> The storage handle type
 * @param <S> The storage proxy type, must implements/extends both {@code <T>} and {@link IStorageProxy}
 * @param <P> The peripheral type, must extends {@link BasePeripheral}
 */
public abstract class BaseDetectorEntity<T, S extends IStorageProxy, P extends BasePeripheral<?>> extends PeripheralBlockEntity<P> implements ICapabilityProvider<BlockEntity, Direction, Object> {

    private static final String RATE_LIMIT_TAG = "RateLimit";

    private final BlockCapability<T, Direction> capability;
    // proxy that will forward X to the output but limit it to maxTransferRate
    private final S proxy = createProxy();
    private volatile long transferRate = 0;
    private S inputStorageCap = null;
    private T zeroStorageCap = null;

    protected BaseDetectorEntity(BlockEntityType<?> tileEntityType, BlockPos pos, BlockState state, BlockCapability<T, Direction> capability) {
        super(tileEntityType, pos, state);
        this.capability = capability;
    }

    @NotNull
    protected abstract S createProxy();

    @NotNull
    protected abstract T getZeroStorage();

    @NotNull
    protected S getStorageProxy() {
        return this.proxy;
    }

    /**
     * @return the transfered amount of stuff in the last tick
     */
    public long getTransferRate() {
        return this.transferRate;
    }

    /**
     * @return the possible maximum transfered amount
     */
    public long getMaxTransferRate() {
        return this.proxy.getMaxTransferRate();
    }

    /**
     * @return the max amount of stuff can be transfered in a tick
     */
    public long getTransferRateLimit() {
        return this.proxy.getTransferRate();
    }

    /**
     * @param rate the max amount of stuff can be transfered in a tick
     */
    public void setTransferRateLimit(long rate) {
        if (this.proxy.getTransferRate() != rate) {
            this.proxy.setTransferRate(rate);
            this.setChanged();
        }
    }

    /**
     * @return the ID of last transfered stuff
     */
    @Nullable
    public String getLastTransferredId() {
        return this.proxy.getLastTransferedId();
    }

    /**
     * @return the ID of ready transfered stuff
     */
    @Nullable
    public String getReadyTransferId() {
        return this.proxy.getReadyTransferId();
    }

    public Direction getInputDirection() {
        return this.getBlockState().getValue(BaseBlock.ORIENTATION).front();
    }

    public Direction getOutputDirection() {
        return this.getBlockState().getValue(BaseBlock.ORIENTATION).front().getOpposite();
    }

    @Override
    public @Nullable Object getCapability(@NotNull BlockEntity object, Direction context) {
        Direction inputDirection = this.getInputDirection();
        Direction outputDirection = this.getOutputDirection();
        if (context == inputDirection) {
            if (this.inputStorageCap != null) {
                this.inputStorageCap = this.getStorageProxy();
            }
            return this.inputStorageCap;
        } else if (context == outputDirection) {
            if (this.zeroStorageCap != null) {
                this.zeroStorageCap = this.getZeroStorage();
            }
            return this.zeroStorageCap;
        }

        return null;
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putLong(RATE_LIMIT_TAG, this.getTransferRateLimit());
    }

    @Override
    public void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider provider) {
        this.proxy.setTransferRate(tag.getLong(RATE_LIMIT_TAG));
        super.loadAdditional(tag, provider);
    }

    @Override
    public <U extends BlockEntity> void handleTick(Level level, BlockState state, BlockEntityType<U> type) {
        super.handleTick(level, state, type);
        if (!level.isClientSide()) {
            this.transferRate = this.proxy.getAndResetTransfered();
        }
    }

    @Nullable
    public T getOutputStorage() {
        Direction outputDirection = this.getOutputDirection();
        return level.getCapability(this.capability, worldPosition.relative(outputDirection), outputDirection.getOpposite());
    }

}
