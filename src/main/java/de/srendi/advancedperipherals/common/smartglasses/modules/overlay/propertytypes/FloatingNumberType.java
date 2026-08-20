package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes;

import de.srendi.advancedperipherals.lib.codec.StreamCodec;
import io.netty.buffer.ByteBuf;

public class FloatingNumberType implements PropertyType<Number, FloatingNumberProperty> {
    public double min;
    public double max;

    @Override
    public void init(FloatingNumberProperty property) {
        min = property.min();
        max = property.max();
    }

    @Override
    public boolean checkIsValid(Object value) {
        return value instanceof Float || value instanceof Double;
    }

    @Override
    public StreamCodec<ByteBuf, ? extends Number> codec(Class<?> type) {
        if (type == Float.TYPE || type == Float.class) {
            return StreamCodec.FLOAT;
        }
        if (type == Double.TYPE || type == Double.class) {
            return StreamCodec.DOUBLE;
        }
        throw new IllegalArgumentException("Unexpected field type: " + type);
    }

    @Override
    public Number fixValue(Number value) {
        if (value instanceof Float) {
            return Math.min(Math.max(value.floatValue(), (float) min), (float) max);
        }
        return Math.min(Math.max(value.doubleValue(), min), max);
    }
}
