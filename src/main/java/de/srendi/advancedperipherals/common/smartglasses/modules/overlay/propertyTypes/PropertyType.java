package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertyTypes;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.ObjectProperty;

import java.lang.reflect.InvocationTargetException;

/**
 * This interface represents a property type. It is used to check if a value is valid for the property and to map the value to the correct type.
 *
 * @param <T> the type of the property value
 * @see ObjectProperty
 */
public interface PropertyType<T> {

    boolean checkIsValid(Object type);

    T mapValue(Object type);

    static PropertyType<?> of(ObjectProperty property) {
        try {
            return property.type().getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException exception) {
            AdvancedPeripherals.exception("An error occurred while trying to create the property type", exception);
        }
        return null;
    }

    void init(Object property);

}
