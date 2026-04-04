package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes;

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
