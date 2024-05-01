/*
 *     Copyright 2024 Intelligence Modding @ https://intelligence-modding.de
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.srendi.advancedperipherals.common.util;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.level.ChunkPos;
import org.apache.logging.log4j.Level;

import java.util.Map;

public class NBTUtil {

    public static Tag toDirectNBT(Object object) {
        // Mostly dan200.computercraft.shared.util toNBTTag method
        // put this map storing changes
        // instead of map serialization use direct map as CompoundNBT
        // assuming that map keys are strings
        if (object == null) {
            return null;
        } else if (object instanceof Boolean bool) {
            return ByteTag.valueOf((byte) (bool ? 1 : 0));
        } else if (object instanceof Integer integer) {
            return IntTag.valueOf(integer);
        } else if (object instanceof Number number) {
            return DoubleTag.valueOf(number.doubleValue());
        } else if (object instanceof String) {
            return StringTag.valueOf(object.toString());
        } else if (object instanceof Map<?, ?> map) {
            CompoundTag nbt = new CompoundTag();

            for (Map.Entry<?, ?> item : map.entrySet()) {
                Tag value = toDirectNBT(item.getValue());
                if (item.getKey() != null && value != null) {
                    nbt.put(item.getKey().toString(), value);
                }
            }
            return nbt;
        } else {
            return null;
        }
    }

    public static CompoundTag fromText(String json) {
        try {
            return json == null ? null : TagParser.parseTag(json);
        } catch (CommandSyntaxException ex) {
            AdvancedPeripherals.debug("Could not parse json data to NBT", Level.ERROR);
            if (APConfig.GENERAL_CONFIG.enableDebugMode.get())
                ex.printStackTrace();
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
