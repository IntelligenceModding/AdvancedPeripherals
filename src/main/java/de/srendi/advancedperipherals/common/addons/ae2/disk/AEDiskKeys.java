package de.srendi.advancedperipherals.common.addons.ae2.disk;

import appeng.api.stacks.AEKeyType;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class AEDiskKeys extends AEKeyType {
    public static final AEDiskKeys INSTANCE = new AEDiskKeys();

    private AEDiskKeys() {
        super(AdvancedPeripherals.getRL("disk_cell"), AEDiskKey.class, Component.translatable("item.advancedperipherals.ae2.disk_key"));
    }

    @Override
    public AEDiskKey loadKeyFromTag(CompoundTag tag) {
        return AEDiskKey.of(tag.getInt("id"));
    }

    @Override
    @Nullable
    public AEDiskKey readFromPacket(FriendlyByteBuf buffer) {
        return AEDiskKey.of(buffer.readVarInt());
    }
}
