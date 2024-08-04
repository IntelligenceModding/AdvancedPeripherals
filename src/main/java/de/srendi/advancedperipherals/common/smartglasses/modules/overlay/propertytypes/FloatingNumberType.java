package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes;

import de.srendi.advancedperipherals.AdvancedPeripherals;

public class FloatingNumberType implements PropertyType<Number> {

    public float min;
    public float max;

    @Override
    public boolean checkIsValid(Object type) {
        return type instanceof Float || type instanceof Double;
    }

    @Override
    public Number mapValue(Object type) {
        AdvancedPeripherals.debug("Mapping value " + type + " to " + Math.min(Math.max((float) type, min), max));
        return Math.min(Math.max((float) type, min), max);
    }

    @Override
    public void init(Object property) {
        FloatingNumberProperty decimalProperty = (FloatingNumberProperty) property;
        min = decimalProperty.min();
        max = decimalProperty.max();
        AdvancedPeripherals.debug("Initialized decimal property with min " + min + " and max " + max);
    }
}

