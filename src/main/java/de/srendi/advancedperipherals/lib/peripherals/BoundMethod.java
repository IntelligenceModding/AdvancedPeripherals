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

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.core.asm.NamedMethod;
import dan200.computercraft.core.asm.PeripheralMethod;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class BoundMethod {
    private final Object target;
    private final String name;
    private final PeripheralMethod method;

    public BoundMethod(@NotNull Object target, @NotNull NamedMethod<PeripheralMethod> method) {
        this.target = target;
        this.name = method.getName();
        this.method = method.getMethod();
    }

    @NotNull public MethodResult apply(@NotNull IComputerAccess access, @NotNull ILuaContext context,
            @NotNull IArguments arguments) throws LuaException {
        return method.apply(target, context, access, arguments);
    }

    @NotNull public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof BoundMethod boundMethod))
            return false;
        return target.equals(boundMethod.target) && name.equals(boundMethod.name) && method.equals(boundMethod.method);
    }

    @Override
    public int hashCode() {
        return Objects.hash(target, name, method);
    }
}
