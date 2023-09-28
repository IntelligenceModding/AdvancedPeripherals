package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertyTypes;

import de.srendi.advancedperipherals.AdvancedPeripherals;

public class FixedPointNumberType implements PropertyType<Number> {

    public long min;
    public long max;

    @Override
    public boolean checkIsValid(Object type) {
        return type instanceof Long || type instanceof Integer || type instanceof Short || type instanceof Byte;
    }

    @Override
    public Number mapValue(Object type) {
        if (type instanceof Long)
            return Math.min(Math.max((long) type, min), max);
        if (type instanceof Integer)
            return Math.min(Math.max((int) type, (int) min), (int) max);
        if (type instanceof Short)
            return Math.min(Math.max((short) type, (short) min), (short) max);
        return Math.min(Math.max((byte) type, (byte) min), (byte) max);
    }

    @Override
    public void init(Object property) {
        FixedPointNumberProperty decimalProperty = (FixedPointNumberProperty) property;
        min = decimalProperty.min();
        max = decimalProperty.max();
        AdvancedPeripherals.debug("Initialized number property with min " + min + " and max " + max);
    }
}

