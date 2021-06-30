package de.srendi.advancedperipherals.common.util;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import net.minecraft.nbt.*;
import org.apache.logging.log4j.Level;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Iterator;
import java.util.Map;

public class NBTUtil {

    public static INBT toDirectNBT(Object object) {
        // Mostly dan200.computercraft.shared.util toNBTTag method
        // put this map storing changes
        // instead of map serialization use direct map as CompoundNBT
        // assuming that map keys are strings
        if (object == null) {
            return null;
        } else if (object instanceof Boolean) {
            return ByteNBT.valueOf((byte) ((Boolean) object ? 1 : 0));
        } else if (object instanceof Integer) {
            return IntNBT.valueOf((Integer) object);
        } else if (object instanceof Number) {
            return DoubleNBT.valueOf(((Number)object).doubleValue());
        } else if (object instanceof String) {
            return StringNBT.valueOf(object.toString());
        } else if (object instanceof Map) {
            Map<?, ?> m = (Map<?, ?>)object;
            CompoundNBT nbt = new CompoundNBT();

            for (Map.Entry<?, ?> item : m.entrySet()) {
                INBT value = toDirectNBT(item.getValue());
                if (item.getKey() != null && value != null) {
                    nbt.put(item.getKey().toString(), value);
                }
            }
            return nbt;
        } else {
            return null;
        }
    }

    public static CompoundNBT fromText(String json) {
        try {
            return json == null ? null : JsonToNBT.parseTag(json);
        } catch (CommandSyntaxException ex) {
            AdvancedPeripherals.debug("Could not parse json data to NBT", Level.ERROR);
            ex.printStackTrace();
            return null;
        }
    }

    public static CompoundNBT fromBinary(String base64) {
        if (base64 == null)
            return null;

        try (InputStream inputStream = Base64.getDecoder().wrap(new ByteArrayInputStream(base64.getBytes()))) {
            return CompressedStreamTools.readCompressed(inputStream);
        } catch (IOException ex) {
            AdvancedPeripherals.debug("Could not parse binary data to NBT", Level.ERROR);
            ex.printStackTrace();
            return null;
        }
    }
}
