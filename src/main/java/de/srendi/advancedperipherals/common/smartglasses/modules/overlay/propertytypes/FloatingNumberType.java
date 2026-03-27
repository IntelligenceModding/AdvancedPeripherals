package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes;

public class FloatingNumberType implements PropertyType<Number> {

    public float min;
    public float max;

    @Override
    public boolean checkIsValid(Object type) {
        return type instanceof Float || type instanceof Double;
    }

    @Override
    public Number mapValue(Object type) {
        return Math.min(Math.max((float) type, min), max);
    }

    @Override
    public void init(Object property) {
        FloatingNumberProperty decimalProperty = (FloatingNumberProperty) property;
        min = decimalProperty.min();
        max = decimalProperty.max();
    }
}

