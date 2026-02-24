package de.srendi.advancedperipherals.common.util;

import net.jpountz.xxhash.XXHash64;
import net.jpountz.xxhash.XXHashFactory;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;

/**
 * Mainly used in AP to hash the fingerprint of items. Successor to the old MD5 based hashes
 * <p>
 * AP currently uses the XXHash library from jpountz see {@link net.jpountz.xxhash.XXHash64}.
 * There are better libraries available, but this one is already shipped within minecraft.
 */
public class FingerprintUtil {

    private static final LRUCache<FingerprintKey, String> FINGERPRINT_CACHE = new LRUCache<>(1024);

    private static final XXHash64 XX_HASH_64 = XXHashFactory.fastestInstance().hash64();
    private static final long SEED = 28122020;

    public static String hash(FingerprintKey key) {
        final String cachedHashStr = FINGERPRINT_CACHE.get(key);
        if (cachedHashStr != null) {
            return cachedHashStr;
        }

        byte[] bytesOfHash = new byte[key.itemId().length() + 1 + 4];

        long hash = XX_HASH_64.hash(bytesOfHash, 0, bytesOfHash.length, SEED);
        final String hashStr = Long.toHexString(hash);

        FINGERPRINT_CACHE.put(key, hashStr);

        return hashStr;
    }

    public record FingerprintKey(
        ResourceLocation itemId,
        int dataHashCode
    ) {
    }
}
