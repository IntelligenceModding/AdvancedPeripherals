package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class FixedPointNumberType implements PropertyType<Number, FixedPointNumberProperty> {
    public long min;
    public long max;

    @Override
    public void init(FixedPointNumberProperty property) {
        min = property.min();
        max = property.max();
    }

    @Override
    public boolean checkIsValid(Object value) {
        return value instanceof Long || value instanceof Integer || value instanceof Short || value instanceof Byte;
    }

    @Override
    public StreamCodec<ByteBuf, ? extends Number> codec(Class<?> type) {
        if (type == Long.TYPE || type == Long.class) {
            return ByteBufCodecs.VAR_LONG;
        }
        if (type == Integer.TYPE || type == Integer.class) {
            return ByteBufCodecs.VAR_INT;
        }
        if (type == Short.TYPE || type == Short.class) {
            return ByteBufCodecs.SHORT;
        }
        if (type == Byte.TYPE || type == Byte.class) {
            return ByteBufCodecs.BYTE;
        }
        throw new IllegalArgumentException("Unexpected field type: " + type);
    }

    @Override
    public Number fixValue(Number value) {
        if (value instanceof Byte valueb) {
            return Math.min(Math.max(valueb.byteValue(), (byte) Math.max(min, Byte.MIN_VALUE)), (byte) Math.min(max, Byte.MAX_VALUE));
        }
        if (value instanceof Short values) {
            return Math.min(Math.max(values.shortValue(), (short) Math.max(min, Short.MIN_VALUE)), (short) Math.max(max, Short.MAX_VALUE));
        }
        if (value instanceof Integer valuei) {
            return Math.min(Math.max(valuei.intValue(), (int) Math.max(min, Integer.MIN_VALUE)), (int) Math.max(max, Integer.MAX_VALUE));
        }
        return Math.min(Math.max(value.longValue(), min), max);
    }
}
