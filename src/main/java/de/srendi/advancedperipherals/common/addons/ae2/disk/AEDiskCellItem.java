package de.srendi.advancedperipherals.common.addons.ae2.disk;

import appeng.core.localization.Tooltips;
import dan200.computercraft.api.filesystem.Mount;
import dan200.computercraft.api.media.IMedia;
import de.srendi.advancedperipherals.common.items.base.BaseItem;
import de.srendi.advancedperipherals.common.setup.APDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.WeakHashMap;

public class AEDiskCellItem extends BaseItem {
    private static final WeakHashMap<MinecraftServer, WeakHashMap<ItemStack, Mount>> MOUNT_HOLDERS = new WeakHashMap<>();

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
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Tooltips.bytesUsed(stack.hasTag() ? stack.getTag().getLong(APDataComponents.DISK_USED_BYTES) : 0, this.getMaxBytes()));
        if (flag.isAdvanced()) {
            CompoundTag tag = stack.getTag();
            if (tag != null && tag.contains(APDataComponents.DISK_ID, Tag.TAG_INT)) {
                tooltip.add(Component.translatable("gui.computercraft.tooltip.disk_id", tag.getInt(APDataComponents.DISK_ID)).withStyle(ChatFormatting.GRAY));
            }
        }
    }

    @Nullable
    public Mount getMount(ItemStack stack) {
        if (!stack.hasTag() || !stack.getTag().contains(APDataComponents.DISK_ID, Tag.TAG_INT)) {
            return null;
        }
        return this.getOrCreateMount(stack);
    }

    @NotNull
    public Mount getOrCreateMount(ItemStack stack) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        return MOUNT_HOLDERS
            .computeIfAbsent(server, (k) -> new WeakHashMap<>())
            .computeIfAbsent(stack, (s) -> this.getMedia().createDataMount(s, server.overworld()));
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
