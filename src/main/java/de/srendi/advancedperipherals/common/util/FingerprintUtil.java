package de.srendi.advancedperipherals.common.util;

import net.minecraft.core.component.DataComponentPatch;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FingerprintUtil {

    private static final LRUCache<DataComponentPatch, String> FINGERPRINT_CACHE = new LRUCache<>(1024);

    @Nullable
    public static String hash(DataComponentPatch patch) {
        if (patch.isEmpty()) {
            return null;
        }
        return FINGERPRINT_CACHE.computeIfAbsent(
            patch,
            (key) -> dan200.computercraft.shared.util.NBTUtil.getNBTHash(
                DataComponentUtil.patchToNbt(key, ServerLifecycleHooks.getCurrentServer().registryAccess())
            )
        );
    }

    @NotNull
    public static String hashOrEmpty(DataComponentPatch patch) {
        if (patch.isEmpty()) {
            return "";
        }
        return hash(patch);
    }
}
