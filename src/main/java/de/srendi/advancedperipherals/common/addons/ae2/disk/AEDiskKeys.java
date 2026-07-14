package de.srendi.advancedperipherals.common.addons.ae2.disk;

import appeng.api.stacks.AEKeyType;
import com.mojang.serialization.MapCodec;
import dan200.computercraft.shared.util.NonNegativeId;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class AEDiskKeys extends AEKeyType {
    public static final AEDiskKeys INSTANCE = new AEDiskKeys();

    private AEDiskKeys() {
        super(AdvancedPeripherals.getRL("disk_cell"), AEDiskKey.class, Component.translatable("item.advancedperipherals.ae2.disk_key"));
    }

    @Override
    public MapCodec<AEDiskKey> codec() {
        return AEDiskKey.MAP_CODEC;
    }

    @Override
    @Nullable
    public AEDiskKey readFromPacket(RegistryFriendlyByteBuf buffer) {
        NonNegativeId id = NonNegativeId.STREAM_CODEC.decode(buffer);
        return AEDiskKey.of(id);
    }
}
