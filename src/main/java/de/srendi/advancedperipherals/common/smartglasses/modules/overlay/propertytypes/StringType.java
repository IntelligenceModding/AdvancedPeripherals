package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes;

public class StringType implements PropertyType<String, StringProperty> {
    @Override
    public void init(StringProperty property) {
        // Nothing to init here, we don't have any filters for strings
    }

    @Override
    public boolean checkIsValid(Object type) {
        return type instanceof String;
    }

    @Override
    public String fixValue(String type) {
        return type;
    }
}
