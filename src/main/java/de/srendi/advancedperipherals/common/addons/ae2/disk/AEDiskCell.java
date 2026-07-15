package de.srendi.advancedperipherals.common.addons.ae2.disk;

import appeng.core.localization.Tooltips;
import dan200.computercraft.api.media.IMedia;
import de.srendi.advancedperipherals.common.items.base.BaseItem;
import de.srendi.advancedperipherals.common.setup.APDataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class AEDiskCell extends BaseItem {
    private final Tier tier;

    public AEDiskCell(Properties properties, Tier tier) {
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
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip, flagIn);
        tooltip.add(Tooltips.bytesUsed(stack.getOrDefault(APDataComponents.DISK_USED_BYTES, 0L), this.getMaxBytes()));
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
