package de.srendi.advancedperipherals.common.util;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public class DataComponentUtil {

    public static Tag toNbt(DataComponentPatch patch, Level level) {
        return DataComponentPatch.CODEC.encodeStart(RegistryOps.create(NbtOps.INSTANCE, level.registryAccess()), patch).getOrThrow();
    }

    public static Tag toNbt(DataComponentPatch patch) {
        return DataComponentPatch.CODEC.encodeStart(RegistryOps.create(NbtOps.INSTANCE, ServerLifecycleHooks.getCurrentServer().registryAccess()), patch).getOrThrow();
    }

}
