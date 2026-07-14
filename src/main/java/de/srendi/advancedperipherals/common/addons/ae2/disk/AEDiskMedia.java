// SPDX-FileCopyrightText: 2025 The CC: Tweaked Developers
//
// SPDX-License-Identifier: MPL-2.0

package de.srendi.advancedperipherals.common.addons.ae2.disk;

import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.filesystem.Mount;
import dan200.computercraft.api.media.IMedia;
import dan200.computercraft.shared.util.DataComponentUtil;
import dan200.computercraft.shared.util.NonNegativeId;
import de.srendi.advancedperipherals.common.setup.APDataComponents;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class AEDiskMedia implements IMedia {
    private final long capacity;

    public AEDiskMedia(long capacity) {
        this.capacity = capacity;
    }

    @Override
    @Nullable
    public String getLabel(HolderLookup.Provider registries, ItemStack stack) {
        return DataComponentUtil.getCustomName(stack);
    }

    @Override
    public boolean setLabel(ItemStack stack, @Nullable String label) {
        DataComponentUtil.setCustomName(stack, label);
        return true;
    }

    @Override
    @Nullable
    public Mount createDataMount(ItemStack stack, ServerLevel level) {
        int id = NonNegativeId.getOrCreate(level.getServer(), stack, APDataComponents.DISK_ID.get(), "disk");
        return ComputerCraftAPI.createSaveDirMount(level.getServer(), "disk/" + id, this.capacity);
    }
}
