package de.srendi.advancedperipherals.common.addons.ae2.disk;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dan200.computercraft.api.filesystem.Mount;
import dan200.computercraft.shared.util.NonNegativeId;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class AEDiskKey extends AEKey {
    public static final MapCodec<AEDiskKey> MAP_CODEC = RecordCodecBuilder.mapCodec(
        builder -> builder
            .group(NonNegativeId.CODEC.fieldOf("id").forGetter(AEDiskKey::getDiskId))
            .apply(builder, AEDiskKey::of)
    );

    private final NonNegativeId id;
    private final ResourceLocation rlId;
    @Nullable
    private final Mount mount;

    private AEDiskKey(NonNegativeId id, @Nullable Mount mount) {
        this.id = id;
        this.rlId = AdvancedPeripherals.getRL("_disk_" + this.id.id());
        this.mount = mount;
    }

    public static AEDiskKey of(NonNegativeId id) {
        return new AEDiskKey(id, null);
    }

    public static AEDiskKey of(NonNegativeId id, Mount mount) {
        return new AEDiskKey(id, mount);
    }

    @Override
    public AEKeyType getType() {
        return AEDiskKeys.INSTANCE;
    }

    public NonNegativeId getDiskId() {
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
        return this.id.equals(key.id);
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public AEKey dropSecondary() {
        return this;
    }

    @Override
    public CompoundTag toTag(HolderLookup.Provider registries) {
        CompoundTag data = new CompoundTag();
        data.putInt("id", this.id.id());
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
    public void writeToPacket(RegistryFriendlyByteBuf buffer) {
        NonNegativeId.STREAM_CODEC.encode(buffer, this.id);
    }

    @Override
    protected Component computeDisplayName() {
        return Component.literal("Disk " + this.id.id());
    }

    @Override
    public void addDrops(long amount, List<ItemStack> drops, Level level, BlockPos pos) {}

    @Override
    public boolean hasComponents() {
        return false;
    }
}
