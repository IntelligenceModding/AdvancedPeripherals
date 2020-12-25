package de.srendi.advancedperipherals.common.blocks.tileentity;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TileEntityList extends WorldSavedData {

    public final List<BlockPos> tileEntities = new ArrayList<>();

    public static TileEntityList get()  {
        DimensionSavedDataManager storage = ServerLifecycleHooks.getCurrentServer().func_241755_D_().getSavedData();
        return storage.getOrCreate(TileEntityList::new, AdvancedPeripherals.MOD_ID);
    }

    public TileEntityList(String name) {
        super(name);
    }

    public TileEntityList() {
        super(AdvancedPeripherals.MOD_ID);
    }

    @Override
    public void read(@Nonnull CompoundNBT nbt) {
        this.tileEntities.clear();
        if (nbt.contains("advancedperipheralstileentitys", Constants.NBT.TAG_LIST)) {
            ListNBT list = nbt.getList("advancedperipheralstileentitys", Constants.NBT.TAG_COMPOUND);
            Minecraft.getInstance().player.sendMessage(new StringTextComponent("Write Debug1"), UUID.randomUUID());
            for (int i = 0; i < list.size(); i++) {
                CompoundNBT entryNBT = list.getCompound(i);
                Minecraft.getInstance().player.sendMessage(new StringTextComponent("Write Debug2"), UUID.randomUUID());
                if (entryNBT.contains("x") && entryNBT.contains("y") && entryNBT.contains("z")) {
                    Minecraft.getInstance().player.sendMessage(new StringTextComponent("Read Debug3"), UUID.randomUUID());
                    BlockPos pos = new BlockPos(entryNBT.getInt("x"), entryNBT.getInt("y"), entryNBT.getInt("z")).toImmutable();
                    this.tileEntities.add(pos);
                }
            }
        }
    }

    @Nonnull
    @Override
    public CompoundNBT write(@Nonnull CompoundNBT nbt) {
        ListNBT list = new ListNBT();
        Minecraft.getInstance().player.sendMessage(new StringTextComponent("Write Debug1"), UUID.randomUUID());
        for (BlockPos entry : this.tileEntities) {
            Minecraft.getInstance().player.sendMessage(new StringTextComponent("Write Debug2" + entry), UUID.randomUUID());
            CompoundNBT entryNBT = new CompoundNBT();
            entryNBT.putInt("x", entry.getX());
            entryNBT.putInt("y", entry.getY());
            entryNBT.putInt("z", entry.getZ());
            list.add(entryNBT);
        }
        nbt.put("advancedperipheralstileentitys", list);
        return nbt;
    }

    public void setTileEntity(World world, BlockPos pos) {
        Minecraft.getInstance().player.sendMessage(new StringTextComponent(world.isRemote + ""), UUID.randomUUID());
        if(!world.isRemote) {
            if (tileEntities.contains(pos)) {
                Minecraft.getInstance().player.sendMessage(new StringTextComponent("Set Debug1 false"), UUID.randomUUID());
                tileEntities.remove(pos);
            } else {
                Minecraft.getInstance().player.sendMessage(new StringTextComponent("Set Debug2 true"), UUID.randomUUID());
                this.tileEntities.add(pos);
            }
        }
        this.markDirty();
    }

    public List<TileEntity> getTileEntitys(ServerWorld world) {
        List<TileEntity> list = new ArrayList<>();
        for(BlockPos blockPos : get().getBlockPositions()) {
            if(world.isBlockLoaded(blockPos)) {
                list.add(world.getTileEntity(blockPos));
            }
        }
        return list;
    }

    public List<BlockPos> getBlockPositions() {
        return tileEntities;
    }
}