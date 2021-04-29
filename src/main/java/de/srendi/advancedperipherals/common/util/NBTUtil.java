package de.srendi.advancedperipherals.common.util;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.JsonToNBT;
import org.apache.logging.log4j.Level;

import java.io.*;
import java.util.Base64;

public class NBTUtil {

    public static CompoundNBT fromText(String json) {
        try {
            return json == null ? null : JsonToNBT.getTagFromJson(json);
        } catch (CommandSyntaxException ex) {
            AdvancedPeripherals.debug("Could not parse json data to NBT", Level.ERROR);
            ex.printStackTrace();
            return null;
        }
    }

    public static String toBinary(CompoundNBT nbt) {
        if (nbt == null)
            return null;

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try (OutputStream stream = Base64.getEncoder().wrap(outputStream)) {
                CompressedStreamTools.writeCompressed(nbt, stream);
            }
            return outputStream.toString();
        } catch (IOException ex) {
            AdvancedPeripherals.debug("Could not parse NBT data to binary", Level.ERROR);
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
