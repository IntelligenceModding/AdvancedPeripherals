package de.srendi.advancedperipherals.common.util;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.Map;

public class NBTUtil {
    private NBTUtil() {}

    public static Tag toDirectNBT(Object object) {
        // Mostly dan200.computercraft.shared.util toNBTTag method
        // put this map storing changes
        // instead of map serialization use direct map as CompoundNBT
        // assuming that map keys are strings
        if (object == null) {
            return null;
        }
        if (object instanceof Boolean bool) {
            return ByteTag.valueOf((byte) (bool ? 1 : 0));
        }
        if (object instanceof Integer integer) {
            return IntTag.valueOf(integer);
        }
        if (object instanceof Number number) {
            return DoubleTag.valueOf(number.doubleValue());
        }
        if (object instanceof String string) {
            return StringTag.valueOf(string);
        }
        if (object instanceof Map<?, ?> map) {
            return mapToNBT(map);
        }
        return null;
    }

    public static CompoundTag mapToNBT(Map<?, ?> map) {
        CompoundTag nbt = new CompoundTag();

        for (Map.Entry<?, ?> item : map.entrySet()) {
            Tag value = toDirectNBT(item.getValue());
            if (item.getKey() != null && value != null) {
                nbt.put(item.getKey().toString(), value);
            }
        }
        return nbt;
    }

    public static CompoundTag fromSNBT(String snbt) {
        try {
            return snbt == null ? null : TagParser.parseTag(snbt);
        } catch (CommandSyntaxException ex) {
            if (APConfig.GENERAL_CONFIG.enableDebugMode.get()) {
                AdvancedPeripherals.debug(org.apache.logging.log4j.Level.ERROR, "Could not parse SNBT to NBT");
                ex.printStackTrace();
            }
            return null;
        }
    }

    public static CompoundTag toNBT(BlockPos pos) {
        CompoundTag data = new CompoundTag();
        data.putInt("x", pos.getX());
        data.putInt("y", pos.getY());
        data.putInt("z", pos.getZ());
        return data;
    }

    public static BlockPos blockPosFromNBT(CompoundTag nbt) {
        return new BlockPos(nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z"));
    }

    public static Pair<Level, BlockPos> levelAndBlockPosFromNBT(MinecraftServer server, CompoundTag nbt) {
        ServerLevel level = server.getLevel(ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(nbt.getString("dim"))));
        return new Pair<>(level, blockPosFromNBT(nbt));
    }

    public static CompoundTag toNBT(Level level, BlockPos pos) {
        CompoundTag data = toNBT(pos);
        data.putString("dim", level.dimension().location().toString());
        return data;
    }

    public static CompoundTag toNBT(ChunkPos pos) {
        CompoundTag data = new CompoundTag();
        data.putInt("x", pos.x);
        data.putInt("z", pos.z);
        return data;
    }

    public static ChunkPos chunkPosFromNBT(CompoundTag nbt) {
        return new ChunkPos(nbt.getInt("x"), nbt.getInt("z"));
    }
}
