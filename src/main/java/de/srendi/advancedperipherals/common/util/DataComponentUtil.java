package de.srendi.advancedperipherals.common.util;

import dan200.computercraft.shared.util.NBTUtil;
import de.srendi.advancedperipherals.common.setup.APDataComponents;

import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.Map;
import java.util.Optional;

public class DataComponentUtil {
    public static CompoundTag patchToNbt(DataComponentPatch patch) {
        return patchToNbt(patch, ServerLifecycleHooks.getCurrentServer().registryAccess());
    }

    public static CompoundTag patchToNbt(DataComponentPatch patch, RegistryAccess registryAccess) {
        return (CompoundTag) DataComponentPatch.CODEC
            .encodeStart(RegistryOps.create(NbtOps.INSTANCE, registryAccess), patch)
            .getOrThrow();
    }

    public static DataComponentPatch nbtToPatch(CompoundTag tag) {
        return nbtToPatch(tag, ServerLifecycleHooks.getCurrentServer().registryAccess());
    }

    public static DataComponentPatch nbtToPatch(CompoundTag tag, RegistryAccess registryAccess) {
        return DataComponentPatch.CODEC
            .parse(RegistryOps.create(NbtOps.INSTANCE, registryAccess), tag)
            .resultOrPartial()
            .orElse(DataComponentPatch.EMPTY);
    }

    public static Map<String, Object> patchToLua(DataComponentPatch patch) {
        return patchToLua(patch, ServerLifecycleHooks.getCurrentServer().registryAccess());
    }

    public static Map<String, Object> patchToLua(DataComponentPatch patch, RegistryAccess registryAccess) {
        // TODO: write an Codec to convert to java objects?
        return (Map<String, Object>) NBTUtil.toLua(patchToNbt(patch, registryAccess));
    }

    public static DataComponentPatch getStoredDataFromItem(ItemStack stack) {
        DataComponentPatch data = stack.getOrDefault(APDataComponents.STORED_DATA, DataComponentPatch.EMPTY);
        Component name = stack.get(DataComponents.CUSTOM_NAME);
        if (name != null) {
            DataComponentPatch.Builder builder = DataComponentPatch.builder();
            data.entrySet().forEach((entry) -> {
                if (entry.getValue().isPresent()) {
                    builder.set((DataComponentType<Object>) entry.getKey(), entry.getValue().get());
                } else {
                    builder.remove(entry.getKey());
                }
            });
            builder.set(DataComponents.CUSTOM_NAME, name);
            data = builder.build();
        }
        return data;
    }

    public static ItemStack patchStoredDataToItem(ItemStack stack, DataComponentPatch data) {
        if (data.isEmpty()) {
            return stack;
        }
        stack = stack.copy();
        Optional<? extends Component> name = data.get(DataComponents.CUSTOM_NAME);
        if (name != null && name.isPresent()) {
            data = data.forget((key) -> key == DataComponents.CUSTOM_NAME);
            stack.set(DataComponents.CUSTOM_NAME, name.get());
        }
        stack.set(APDataComponents.STORED_DATA, data);
        return stack;
    }
}
