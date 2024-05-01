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
