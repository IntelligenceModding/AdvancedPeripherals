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

public class FixedPointNumberType implements PropertyType<Number> {

    public long min;
    public long max;

    @Override
    public boolean checkIsValid(Object type) {
        return type instanceof Long || type instanceof Integer || type instanceof Short || type instanceof Byte;
    }

    @Override
    public Number mapValue(Object type) {
        if (type instanceof Long)
            return Math.min(Math.max((long) type, min), max);
        if (type instanceof Integer)
            return Math.min(Math.max((int) type, (int) min), (int) max);
        if (type instanceof Short)
            return Math.min(Math.max((short) type, (short) min), (short) max);
        return Math.min(Math.max((byte) type, (byte) min), (byte) max);
    }

    @Override
    public void init(Object property) {
        FixedPointNumberProperty decimalProperty = (FixedPointNumberProperty) property;
        min = decimalProperty.min();
        max = decimalProperty.max();
        AdvancedPeripherals.debug("Initialized number property with min " + min + " and max " + max);
    }
}
