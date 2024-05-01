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

public class FloatingNumberType implements PropertyType<Number> {

    public double min;
    public double max;

    @Override
    public boolean checkIsValid(Object type) {
        return type instanceof Float || type instanceof Double;
    }

    @Override
    public Number mapValue(Object type) {
        AdvancedPeripherals.debug("Mapping value " + type + " to " + Math.min(Math.max((double) type, min), max));
        return Math.min(Math.max((double) type, min), max);
    }

    @Override
    public void init(Object property) {
        FloatingNumberProperty decimalProperty = (FloatingNumberProperty) property;
        min = decimalProperty.min();
        max = decimalProperty.max();
        AdvancedPeripherals.debug("Initialized decimal property with min " + min + " and max " + max);
    }
}
