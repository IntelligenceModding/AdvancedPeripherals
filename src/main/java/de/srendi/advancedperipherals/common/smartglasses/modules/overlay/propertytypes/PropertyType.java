package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertytypes;

import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.ObjectProperty;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.lang.reflect.InvocationTargetException;

/**
 * This interface represents a property type. It is used to check if a value is valid for the property and to map the value to the correct type.
 *
 * @param <T> the type of the property value
 * @param <A> the type of the property annotation
 * @see ObjectProperty
 */
public interface PropertyType<T, A> {
    void init(A property);

    boolean checkIsValid(Object value);

    StreamCodec<? super RegistryFriendlyByteBuf, ? extends T> codec(Class<?> type);

    T fixValue(T value);

    static PropertyType<?, ?> of(ObjectProperty property) {
        try {
            return property.value().getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException exception) {
            return null;
        }
    }
}
