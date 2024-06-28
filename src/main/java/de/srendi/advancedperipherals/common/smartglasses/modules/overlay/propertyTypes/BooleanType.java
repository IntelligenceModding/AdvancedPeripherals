package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertyTypes;

public class BooleanType implements PropertyType<Boolean> {

    @Override
    public boolean checkIsValid(Object type) {
        return type instanceof Boolean;
    }

    @Override
    public Boolean mapValue(Object type) {
        return (Boolean) type;
    }

    @Override
    public void init(Object property) {
        // Nothing to init here, we don't have any filters for booleans
    }
}

