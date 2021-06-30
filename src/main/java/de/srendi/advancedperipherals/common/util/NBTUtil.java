package de.srendi.advancedperipherals.common.util;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.JsonToNBT;
import org.apache.logging.log4j.Level;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

public class NBTUtil {

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
