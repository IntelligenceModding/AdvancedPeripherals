package de.srendi.advancedperipherals.common.util;

import net.jpountz.xxhash.XXHash64;
import net.jpountz.xxhash.XXHashFactory;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 * Mainly used in AP to hash the fingerprint of items. Successor to the old MD5 based hashes
 * <p>
 * AP currently uses the XXHash library from jpountz see {@link XXHash64}.
 * There are better libraries available, but this one is already shipped within minecraft.
 */
public class FingerprintUtil {

    private static final HashMap<FingerprintKey, String> FINGERPRINT_CACHE = new HashMap<>();

    private static final XXHash64 XX_HASH_64 = XXHashFactory.fastestInstance().hash64();
    private static final long SEED = 28122020;

    public static String hash(FingerprintKey fingerprint) {

        if (FINGERPRINT_CACHE.containsKey(fingerprint)) {
            return FINGERPRINT_CACHE.get(fingerprint);
        }

        byte[] bytesOfHash = fingerprint.toString().getBytes(StandardCharsets.UTF_8);

        long hash = XX_HASH_64.hash(bytesOfHash, 0, bytesOfHash.length, SEED);

        FINGERPRINT_CACHE.put(fingerprint, Long.toString(hash));

        return Long.toString(hash);
    }

    public record FingerprintKey(ResourceLocation itemId, @Nullable Integer dataHashCode,
                                 @Nullable String displayName) {
    }

}
