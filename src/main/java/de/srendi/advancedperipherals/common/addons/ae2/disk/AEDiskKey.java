package de.srendi.advancedperipherals.common.addons.ae2.disk;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import dan200.computercraft.api.filesystem.Mount;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class AEDiskKey extends AEKey {
    private final int id;
    private final ResourceLocation rlId;
    @Nullable
    private final Mount mount;

    private AEDiskKey(int id, @Nullable Mount mount) {
        this.id = id;
        this.rlId = AdvancedPeripherals.getRL("_disk_" + this.id);
        this.mount = mount;
    }

    public static AEDiskKey of(int id) {
        return new AEDiskKey(id, null);
    }

    public static AEDiskKey of(int id, Mount mount) {
        return new AEDiskKey(id, mount);
    }

    @Override
    public AEKeyType getType() {
        return AEDiskKeys.INSTANCE;
    }

    public int getDiskId() {
        return this.id;
    }

    @Nullable
    public Mount getMount() {
        return this.mount;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AEDiskKey key)) {
            return false;
        }
        return this.id == key.id;
    }

    @Override
    public int hashCode() {
        return this.id;
    }

    @Override
    public AEKey dropSecondary() {
        return this;
    }

    @Override
    public CompoundTag toTag() {
        CompoundTag data = new CompoundTag();
        data.putInt("id", this.id);
        return data;
    }

    @Override
    public Object getPrimaryKey() {
        return this.id;
    }

    @Override
    public ResourceLocation getId() {
        return this.rlId;
    }

    @Override
    public void writeToPacket(FriendlyByteBuf buffer) {
        buffer.writeVarInt(this.id);
    }

    @Override
    protected Component computeDisplayName() {
        return Component.literal("Disk " + this.id);
    }

    @Override
    public void addDrops(long amount, List<ItemStack> drops, Level level, BlockPos pos) {}
}
