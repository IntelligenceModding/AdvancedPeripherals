package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertyTypes;

public class StringType implements PropertyType<String> {

    @Override
    public boolean checkIsValid(Object type) {
        return type instanceof String;
    }

    @Override
    public String mapValue(Object type) {
        return (String) type;
    }

    @Override
    public void init(Object property) {
        // Nothing to init here, we don't have any filters for strings
    }
}

