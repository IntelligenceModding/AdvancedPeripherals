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
package de.srendi.advancedperipherals.lib.peripherals;

import dan200.computercraft.api.lua.MethodResult;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface IPeripheralCheck<T> {

    @Nullable MethodResult check(T context);

    default IPeripheralCheck<T> checkAlso(IPeripheralCheck<T> check) {
        return new ChainedPeripheralCheck<>(this, check);
    }

    class ChainedPeripheralCheck<T1> implements IPeripheralCheck<T1> {
        private final IPeripheralCheck<T1> first;
        private final IPeripheralCheck<T1> second;

        private ChainedPeripheralCheck(IPeripheralCheck<T1> first, IPeripheralCheck<T1> second) {
            this.first = first;
            this.second = second;
        }

        @Override
        public @Nullable MethodResult check(T1 context) {
            MethodResult firstCheck = first.check(context);
            if (firstCheck != null)
                return firstCheck;
            return second.check(context);
        }
    }
}
