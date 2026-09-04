package de.srendi.advancedperipherals.common.addons.ae2.disk;

import appeng.api.stacks.AEKeyType;
import com.mojang.serialization.MapCodec;
import dan200.computercraft.shared.util.NonNegativeId;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.setup.APTranslations;
import net.minecraft.network.RegistryFriendlyByteBuf;
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
