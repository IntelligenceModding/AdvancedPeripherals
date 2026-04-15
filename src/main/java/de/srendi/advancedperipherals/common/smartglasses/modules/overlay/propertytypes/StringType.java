package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class StringType implements PropertyType<String, StringProperty> {
    @Override
    public void init(StringProperty property) {
        // Nothing to init here, we don't have any filters for strings
    }

    @Override
    public boolean checkIsValid(Object type) {
        return type instanceof String;
    }

    @Override
    public StreamCodec<ByteBuf, String> codec(Class<?> type) {
        return ByteBufCodecs.STRING_UTF8;
    }

    @Override
    public String fixValue(String type) {
        return type;
    }
}
