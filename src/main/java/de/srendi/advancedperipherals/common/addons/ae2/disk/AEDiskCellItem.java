package de.srendi.advancedperipherals.common.addons.ae2.disk;

import appeng.core.localization.Tooltips;
import dan200.computercraft.api.filesystem.Mount;
import dan200.computercraft.api.media.IMedia;
import dan200.computercraft.shared.util.NonNegativeId;
import de.srendi.advancedperipherals.common.items.base.BaseItem;
import de.srendi.advancedperipherals.common.setup.APDataComponents;
import de.srendi.advancedperipherals.common.util.TransientValueHolder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AEDiskCellItem extends BaseItem {
    private final Tier tier;

    public AEDiskCellItem(Properties properties, Tier tier) {
        super(properties);
        this.tier = tier;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public long getMaxBytes() {
        return this.tier.bytes;
    }

    public double getIdleDrain() {
        return this.tier.megaBytes * 8;
    }

    public IMedia getMedia() {
        return this.tier.media;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Tooltips.bytesUsed(stack.getOrDefault(APDataComponents.DISK_USED_BYTES, 0L), this.getMaxBytes()));
        if (flag.isAdvanced()) {
            NonNegativeId id = stack.getOrDefault(APDataComponents.DISK_ID, null);
            if (id != null) {
                tooltip.add(Component.translatable("gui.computercraft.tooltip.disk_id", id.id()).withStyle(ChatFormatting.GRAY));
            }
        }
    }

    @Nullable
    public Mount getMount(ItemStack stack) {
        if (!stack.has(APDataComponents.DISK_ID)) {
            return null;
        }
        return this.getOrCreateMount(stack);
    }

    @NotNull
    public Mount getOrCreateMount(ItemStack stack) {
        Mount mount = stack.getOrDefault(APDataComponents._MOUNT_HOLDER, TransientValueHolder.<Mount>nil()).value();
        if (mount == null) {
            mount = this.getMedia().createDataMount(stack, ServerLifecycleHooks.getCurrentServer().overworld());
            stack.set(APDataComponents._MOUNT_HOLDER, new TransientValueHolder<>(mount));
        }
        return mount;
    }

    public enum Tier {
        DISK_1M(1),
        DISK_4M(4),
        DISK_16M(16),
        DISK_64M(64),
        DISK_256M(256);

        public final int megaBytes;
        public final long bytes;
        public final IMedia media;

        Tier(int megaBytes) {
            this.megaBytes = megaBytes;
            this.bytes = (long) this.megaBytes * 1000 * 1000;
            this.media = new AEDiskMedia(this.bytes);
        }
    }
}
