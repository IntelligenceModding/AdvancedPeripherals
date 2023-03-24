package de.srendi.advancedperipherals.common.blocks.blockentities;

import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.DistanceDetectorPeripheral;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralBlockEntity;
import de.srendi.advancedperipherals.common.setup.APBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DistanceDetectorEntity extends PeripheralBlockEntity<DistanceDetectorPeripheral> {

    private float distance = 0;
    private boolean showLaser = true;

    public DistanceDetectorEntity(BlockPos pos, BlockState state) {
        super(APBlockEntityTypes.DISTANCE_DETECTOR.get(), pos, state);
    }

    @NotNull
    @Override
    protected DistanceDetectorPeripheral createPeripheral() {
        return new DistanceDetectorPeripheral(this);
    }

    public void setShowLaser(boolean showLaser) {
        if(this.showLaser != showLaser)
            level.setBlockAndUpdate(getBlockPos(), getBlockState());

        this.showLaser = showLaser;
    }

    public void setDistance(float distance) {
        if(this.distance != distance)
            level.setBlockAndUpdate(getBlockPos(), getBlockState());
        this.distance = distance;
    }

    public float getDistance() {
        return distance;
    }

    public boolean getLaserVisibility() {
        return showLaser;
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        if (level.isClientSide && net.getDirection() == PacketFlow.CLIENTBOUND) {
            //Handle the update tag when we are on the client
            handleUpdateTag(pkt.getTag());
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("distance", distance);
        tag.putBoolean("showLaser", showLaser);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        this.distance = tag.getFloat("distance");
        this.showLaser = tag.getBoolean("showLaser");
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

}
