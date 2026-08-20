// SPDX-FileCopyrightText: 2025 The CC: Tweaked Developers
//
// SPDX-License-Identifier: MPL-2.0

package de.srendi.advancedperipherals.common.addons.ae2.disk;

import dan200.computercraft.api.filesystem.Mount;
import dan200.computercraft.api.media.IMedia;
import dan200.computercraft.shared.media.MountMedia;
import dan200.computercraft.shared.media.items.DiskItem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class AEDiskMedia implements IMedia {
    private final MountMedia wrapped;

    public AEDiskMedia(long capacity) {
        this.wrapped = new MountMedia("disk", DiskItem::getDiskID, DiskItem::setDiskID, () -> (int) capacity);
    }

    @Override
    @Nullable
    public String getLabel(ItemStack stack) {
        return this.wrapped.getLabel(stack);
    }

    @Override
    public boolean setLabel(ItemStack stack, @Nullable String label) {
        return this.wrapped.setLabel(stack, label);
    }

    @Override
    @Nullable
    public Mount createDataMount(ItemStack stack, ServerLevel level) {
        return this.wrapped.createDataMount(stack, level);
    }
}
