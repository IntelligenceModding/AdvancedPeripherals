package de.srendi.advancedperipherals.common.blocks.base;

import com.google.common.collect.Lists;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class TileEntityList extends WorldSavedData {

    private static final TileEntityList clientInstance = new TileEntityList();
    public final HashSet<WorldPos> tileEntities = new HashSet<>();

    public TileEntityList() {
        super(AdvancedPeripherals.MOD_ID);
    }

    public static TileEntityList get(World world) {
        if (!world.isClientSide) {
            DimensionSavedDataManager storage = ServerLifecycleHooks.getCurrentServer().overworld().getDataStorage();
            return storage.computeIfAbsent(TileEntityList::new, AdvancedPeripherals.MOD_ID);
        } else {
            return clientInstance;
        }
    }

    public static World getWorldFromKey(RegistryKey<World> key) {
        for (ServerWorld serverWorld : ServerLifecycleHooks.getCurrentServer().getAllLevels()) {
            if (serverWorld.dimension().getRegistryName().equals(key.getRegistryName()))
                return serverWorld;
        }
        return null;
    }

    @Override
    public void load(@Nonnull CompoundNBT nbt) {
        this.tileEntities.clear();
        if (nbt.contains("advancedperipheralstileentities", Constants.NBT.TAG_LIST)) {
            ListNBT list = nbt.getList("advancedperipheralstileentities", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                CompoundNBT entryNBT = list.getCompound(i);
                WorldPos pos = WorldPos.read(entryNBT);
                if (pos != null) {
                    this.tileEntities.add(pos);
                } else {
                    this.setDirty();
                }
            }
        }
    }

    @Nonnull
    @Override
    public CompoundNBT save(@Nonnull CompoundNBT nbt) {
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
        if (!world.isClientSide) {
            if (tileEntities.contains(pos)) {
                tileEntities.remove(pos);
            } else {
                this.tileEntities.add(pos);
            }
        }
        this.setDirty();
    }

    public void setTileEntity(World world, WorldPos pos, boolean force) {
        clearTileEntities();
        if (!world.isClientSide) {
            if (force) {
                if (!tileEntities.contains(pos))
                    tileEntities.add(pos);
            } else {
                tileEntities.remove(pos);
            }
        }
        this.setDirty();
    }

    public List<TileEntity> getTileEntities() {
        List<TileEntity> list = new ArrayList<>();
        for (WorldPos next : new ArrayList<>(tileEntities)) {
            World currentWorld = getWorldFromKey(next.getWorld());
            if (currentWorld.isEmptyBlock(next.getBlockPos()) || !currentWorld.isLoaded(next.getBlockPos()))
                setTileEntity(currentWorld, next, false); //No block here anymore.
            if (currentWorld.getBlockEntity(next.getBlockPos()) == null)
                setTileEntity(currentWorld, next, false); //No tile entity here anymore.
            list.add(currentWorld.getBlockEntity(next.getBlockPos()));
        }
        return list;
    }

    public void clearTileEntities() {
        for (WorldPos next : new ArrayList<>(tileEntities)) {
            World currentWorld = getWorldFromKey(next.getWorld());
            if (currentWorld.isEmptyBlock(next.getBlockPos()) || !currentWorld.isLoaded(next.getBlockPos()))
                if (tileEntities.contains(next))
                    tileEntities.remove(next);
            if (currentWorld.getBlockEntity(next.getBlockPos()) == null)
                if (tileEntities.contains(next))
                    tileEntities.remove(next);
        }
    }

    public HashSet<WorldPos> getBlockPositions() {
        return tileEntities;
    }

}