package de.srendi.advancedperipherals.common.util;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FingerprintUtil {

    private static final LRUCache<CompoundTag, String> FINGERPRINT_CACHE = new LRUCache<>(1024);

    @Nullable
    public static String hash(CompoundTag tag) {
        if (tag == null || tag.isEmpty()) {
            return null;
        }
        String cached = FINGERPRINT_CACHE.get(tag);
        if (cached != null) {
            return cached;
        }
        return FINGERPRINT_CACHE.computeIfAbsent(
            tag.copy(),
            dan200.computercraft.shared.util.NBTUtil::getNBTHash
        );
    }

    @NotNull
    public static String hashOrEmpty(CompoundTag tag) {
        if (tag == null || tag.isEmpty()) {
            return "";
        }
        return hash(tag);
    }
}
