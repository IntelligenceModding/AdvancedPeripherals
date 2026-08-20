package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes;

import de.srendi.advancedperipherals.lib.codec.StreamCodec;
import io.netty.buffer.ByteBuf;

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
            return StreamCodec.VAR_LONG;
        }
        if (type == Integer.TYPE || type == Integer.class) {
            return StreamCodec.VAR_INT;
        }
        if (type == Short.TYPE || type == Short.class) {
            return StreamCodec.SHORT;
        }
        if (type == Byte.TYPE || type == Byte.class) {
            return StreamCodec.BYTE;
        }
        throw new IllegalArgumentException("Unexpected field type: " + type);
    }

    @Override
    public Number fixValue(Number value) {
        if (value instanceof Byte valueb) {
            return Math.min(Math.max(valueb.byteValue(), (byte) Math.max(min, Byte.MIN_VALUE)), (byte) Math.min(max, Byte.MAX_VALUE));
        }
        if (value instanceof Short values) {
            return Math.min(Math.max(values.shortValue(), (short) Math.max(min, Short.MIN_VALUE)), (short) Math.min(max, Short.MAX_VALUE));
        }
        if (value instanceof Integer valuei) {
            return Math.min(Math.max(valuei.intValue(), (int) Math.max(min, Integer.MIN_VALUE)), (int) Math.min(max, Integer.MAX_VALUE));
        }
        return Math.min(Math.max(value.longValue(), min), max);
    }
}
