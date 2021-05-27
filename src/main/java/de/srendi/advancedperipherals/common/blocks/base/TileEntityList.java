package de.srendi.advancedperipherals.common.blocks.base;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.util.WorldPos;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class TileEntityList extends WorldSavedData {

    private static final TileEntityList clientInstance = new TileEntityList();
    public final HashSet<WorldPos> tileEntities = new HashSet<>();

    public TileEntityList() {
        super(AdvancedPeripherals.MOD_ID);
    }

    public static TileEntityList get(World world) {
        if (!world.isRemote) {
            DimensionSavedDataManager storage = ServerLifecycleHooks.getCurrentServer().func_241755_D_().getSavedData();
            return storage.getOrCreate(TileEntityList::new, AdvancedPeripherals.MOD_ID);
        } else {
            return clientInstance;
        }
    }

    @Override
    public void read(@Nonnull CompoundNBT nbt) {
        this.tileEntities.clear();
        if (nbt.contains("advancedperipheralstileentities", Constants.NBT.TAG_LIST)) {
            ListNBT list = nbt.getList("advancedperipheralstileentities", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                CompoundNBT entryNBT = list.getCompound(i);
                WorldPos pos = WorldPos.read(entryNBT);
                if (pos != null) {
                    this.tileEntities.add(pos);
                } else {
                    this.markDirty();
                }
            }
        }
    }

    @Nonnull
    @Override
    public CompoundNBT write(@Nonnull CompoundNBT nbt) {
        ListNBT list = new ListNBT();
        for (WorldPos entry : this.tileEntities) {
            CompoundNBT entryNBT = new CompoundNBT();
            list.add(entry.write(entryNBT));
        }
        nbt.put("advancedperipheralstileentities", list);
        return nbt;
    }

    public void setTileEntity(World world, WorldPos pos) {
        clearTileEntities();
        if (!world.isRemote) {
            if (tileEntities.contains(pos)) {
                tileEntities.remove(pos);
            } else {
                this.tileEntities.add(pos);
            }
        }
        this.markDirty();
    }

    public void setTileEntity(World world, WorldPos pos, boolean force) {
        clearTileEntities();
        if (!world.isRemote) {
            if (force) {
                if (!tileEntities.contains(pos))
                    tileEntities.add(pos);
            } else {
                tileEntities.remove(pos);
            }
        }
        this.markDirty();
    }

    public List<TileEntity> getTileEntities() {
        List<TileEntity> list = new ArrayList<>();
        for (WorldPos next : new ArrayList<>(tileEntities)) {
            World currentWorld = getWorldFromKey(next.getWorld());
            if (currentWorld.isAirBlock(next.getBlockPos()) || !currentWorld.isBlockLoaded(next.getBlockPos()))
                setTileEntity(currentWorld, next, false); //No block here anymore.
            if (currentWorld.getTileEntity(next.getBlockPos()) == null)
                setTileEntity(currentWorld, next, false); //No tile entity here anymore.
            list.add(currentWorld.getTileEntity(next.getBlockPos()));
        }
        return list;
    }

    public void clearTileEntities() {
        for (WorldPos next : new ArrayList<>(tileEntities)) {
            World currentWorld = getWorldFromKey(next.getWorld());
            AdvancedPeripherals.debug("DEBUG1");
            if (currentWorld.isAirBlock(next.getBlockPos()) || !currentWorld.isBlockLoaded(next.getBlockPos()))
                if (tileEntities.contains(next))
                    tileEntities.remove(next);
            if (currentWorld.getTileEntity(next.getBlockPos()) == null)
                if (tileEntities.contains(next))
                    tileEntities.remove(next);
        }
    }

    public HashSet<WorldPos> getBlockPositions() {
        return tileEntities;
    }

    public static World getWorldFromKey(RegistryKey<World> key) {
        for (ServerWorld serverWorld : ServerLifecycleHooks.getCurrentServer().getWorlds()) {
            if (serverWorld.getDimensionKey().getLocation().equals(key.getLocation()))
                return serverWorld;
        }
        return null;
    }

}