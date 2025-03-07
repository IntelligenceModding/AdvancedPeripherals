package de.srendi.advancedperipherals.common.util.inventory;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.util.StringUtil;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ChemicalUtil {

    @NotNull
    public static String getFingerprint(@NotNull ChemicalStack stack) {
        // A pretty lame fingerprint, a chemical stack does not have any components or other stuff
        String fingerprint = getRegistryKey(stack).toString();
        try {
            byte[] bytesOfHash = fingerprint.getBytes(StandardCharsets.UTF_8);
            MessageDigest md = MessageDigest.getInstance("MD5");
            return StringUtil.toHexString(md.digest(bytesOfHash));
        } catch (NoSuchAlgorithmException ex) {
            AdvancedPeripherals.debug("Could not parse fingerprint.", org.apache.logging.log4j.Level.ERROR);
            ex.printStackTrace();
        }
        return "";
    }

    public static ResourceLocation getRegistryKey(Chemical fluid) {
        return MekanismAPI.CHEMICAL_REGISTRY.getKey(fluid);
    }

    public static ResourceLocation getRegistryKey(ChemicalStack fluid) {
        return MekanismAPI.CHEMICAL_REGISTRY.getKey(fluid.copy().getChemical());
    }
}
