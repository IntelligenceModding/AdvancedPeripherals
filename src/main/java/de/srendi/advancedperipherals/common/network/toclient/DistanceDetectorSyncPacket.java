package de.srendi.advancedperipherals.common.network.toclient;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.blocks.blockentities.DistanceDetectorEntity;
import de.srendi.advancedperipherals.common.network.base.IPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

public class DistanceDetectorSyncPacket implements IPacket {

    private final BlockPos position;
    private final ResourceKey<Level> world;
    private final float distance;
    private final boolean showLaser;

    public DistanceDetectorSyncPacket(BlockPos position, ResourceKey<Level> world, float distance, boolean showLaser) {
        this.position = position;
        this.world = world;
        this.distance = distance;
        this.showLaser = showLaser;
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        ClientLevel level = Minecraft.getInstance().level;
        if (!level.dimension().equals(world))
            return;

        BlockEntity tileEntity = level.getBlockEntity(position);
        if (tileEntity == null) {
            AdvancedPeripherals.debug("Could not update distance detector at " + position + " in world " + world.location()
                    + " because the world or the tile entity couldn't be found. Ignoring it");
            return;
        }
        if (tileEntity instanceof DistanceDetectorEntity detector) {
            detector.setCurrentDistance(distance);
            detector.setShowLaser(showLaser);
        }
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(position);
        buffer.writeResourceKey(world);
        buffer.writeFloat(distance);
        buffer.writeBoolean(showLaser);
    }

    public static DistanceDetectorSyncPacket decode(FriendlyByteBuf buffer) {
        return new DistanceDetectorSyncPacket(buffer.readBlockPos(), buffer.readResourceKey(Registry.DIMENSION_REGISTRY), buffer.readFloat(), buffer.readBoolean());
    }
}
