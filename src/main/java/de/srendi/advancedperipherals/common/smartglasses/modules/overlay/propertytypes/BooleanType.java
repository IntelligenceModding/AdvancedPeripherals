package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes;

public class BooleanType implements PropertyType<Boolean, BooleanProperty> {
    private String getterPrefix;

    @Override
    public void init(BooleanProperty property) {
        this.getterPrefix = property.getterPrefix();
    }

    public String getGetterPrefix() {
        return this.getterPrefix;
    }

    @Override
    public boolean checkIsValid(Object type) {
        return type instanceof Boolean;
    }

    @Override
    public Boolean fixValue(Boolean type) {
        return type;
    }
}
