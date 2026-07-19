package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes;

import de.srendi.advancedperipherals.common.util.StringUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class StringType implements PropertyType<String, StringProperty> {
    private boolean utf8;

    @Override
    public void init(StringProperty property) {
        this.utf8 = property.utf8();
    }

    @Override
    public boolean checkIsValid(Object type) {
        return type instanceof String;
    }

    @Override
    public StreamCodec<ByteBuf, String> codec(Class<?> type) {
        // TODO: is non-utf8 string safe to use utf8 codec?
        // we don't have non-utf8 string right now anyways.
        return ByteBufCodecs.STRING_UTF8;
    }

    @Override
    public String fixValue(String type) {
        if (this.utf8) {
            type = StringUtil.byteStringToUTF8(type);
        }
        return type;
    }
}
