/*
 *     Copyright 2024 Intelligence Modding @ https://intelligence-modding.de
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.srendi.advancedperipherals.common.smartglasses.modules.overlay.propertyTypes;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.smartglasses.modules.overlay.ObjectProperty;

import java.lang.reflect.InvocationTargetException;

/**
 * This interface represents a property type. It is used to check if a value is
 * valid for the property and to map the value to the correct type.
 *
 * @param <T>
 *            the type of the property value
 * @see ObjectProperty
 */
public interface PropertyType<T> {

    boolean checkIsValid(Object type);

    T mapValue(Object type);

    static PropertyType<?> of(ObjectProperty property) {
        try {
            return property.type().getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException
                | InvocationTargetException exception) {
            AdvancedPeripherals.exception("An error occurred while trying to create the property type", exception);
        }
        return null;
    }

    void init(Object property);

}
