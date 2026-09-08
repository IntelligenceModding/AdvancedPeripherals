package de.srendi.advancedperipherals.common.util.fakelevel;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;

public class FakeLevel extends ClientLevel {
    public FakeLevel(ClientPacketListener connection, ClientLevel level) {
        super(
            connection,
            level.getLevelData(),
            level.dimension(),
            level.dimensionTypeRegistration(),
            2,
            1,
            level.getProfilerSupplier(),
            null,
            false,
            0
        );
    }

    @Override
    public void unload(LevelChunk chunk) {
    }

    @Override
    public void onChunkLoaded(ChunkPos chunkPos) {
    }

    @Override
    public void sendBlockUpdated(BlockPos pos, BlockState oldState, BlockState newState, int flags) {
    }

    @Override
    public void setBlocksDirty(BlockPos blockPos, BlockState oldState, BlockState newState) {
    }

    @Override
    public void setSectionDirtyWithNeighbors(int sectionX, int sectionY, int sectionZ) {
    }

    @Override
    public void destroyBlockProgress(int breakerId, BlockPos pos, int progress) {
    }

    @Override
    public void globalLevelEvent(int id, BlockPos pos, int data) {
    }

    @Override
    public void levelEvent(Player player, int type, BlockPos pos, int data) {
    }

    @Override
    public void addParticle(ParticleOptions particleData, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
    }

    @Override
    public void addParticle(
        ParticleOptions particleData,
        boolean forceAlwaysRender,
        double x,
        double y,
        double z,
        double xSpeed,
        double ySpeed,
        double zSpeed
    ) {
    }

    @Override
    public void addAlwaysVisibleParticle(ParticleOptions particleData, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
    }

    @Override
    public void addAlwaysVisibleParticle(
        ParticleOptions particleData,
        boolean ignoreRange,
        double x,
        double y,
        double z,
        double xSpeed,
        double ySpeed,
        double zSpeed
    ) {
    }
}
