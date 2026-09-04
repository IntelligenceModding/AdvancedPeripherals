package de.srendi.advancedperipherals.common.addons.ae2.disk;

import appeng.api.stacks.AEKeyType;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.setup.APTranslations;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class AEDiskKeys extends AEKeyType {
    public static final ResourceLocation ID = AdvancedPeripherals.getRL("disk_cell");
    public static final AEDiskKeys INSTANCE = new AEDiskKeys();

    private AEDiskKeys() {
        super(ID, AEDiskKey.class, Component.translatable(APTranslations.AE_DISK_DESCRIPTION));
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
