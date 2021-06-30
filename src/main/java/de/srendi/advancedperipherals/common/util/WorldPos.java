package de.srendi.advancedperipherals.common.util;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.Objects;

public class WorldPos {

    private int x;
    private int y;
    private int z;

    private RegistryKey<World> world;

    public WorldPos(int x, int y, int z, World world) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world.dimension();
    }

    public WorldPos(int x, int y, int z, RegistryKey<World> world) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
    }

    public WorldPos(BlockPos pos, World world) {
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
        this.world = world.dimension();
    }

    public static WorldPos read(CompoundNBT nbt) {
        if (!(nbt.contains("x") && nbt.contains("y") && nbt.contains("z") && nbt.contains("world")))
            return null;
        int x = nbt.getInt("x");
        int y = nbt.getInt("y");
        int z = nbt.getInt("z");
        String world = nbt.getString("world");
        return new WorldPos(x, y, z, RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(world)));
    }

    public CompoundNBT write(CompoundNBT nbt) {
        nbt.putInt("x", this.x);
        nbt.putInt("y", this.y);
        nbt.putInt("z", this.z);
        nbt.putString("world", this.world.getRegistryName().toString());
        return nbt;
    }

    public BlockPos getBlockPos() {
        return new BlockPos(x, y, z);
    }

    @Override
    public String toString() {
        return "WorldPos{x=" + x + ", y=" + y + ", z=" + z + ", world=`" + world.getRegistryName() + '}';
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public RegistryKey<World> getWorld() {
        return world;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorldPos worldPos = (WorldPos) o;
        return x == worldPos.x && y == worldPos.y && z == worldPos.z && Objects.equals(world, worldPos.world);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z, world);
    }
}
