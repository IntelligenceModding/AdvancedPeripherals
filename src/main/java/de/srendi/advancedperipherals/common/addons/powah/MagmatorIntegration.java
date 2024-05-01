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
package de.srendi.advancedperipherals.common.addons.powah;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.lib.peripherals.BlockEntityIntegrationPeripheral;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import owmii.powah.block.magmator.MagmatorTile;

public class MagmatorIntegration extends BlockEntityIntegrationPeripheral<MagmatorTile> {
    protected MagmatorIntegration(BlockEntity entity) {
        super(entity);
    }

    @NotNull @Override
    public String getType() {
        return "magmator";
    }

    @LuaFunction(mainThread = true)
    public final String getName() {
        return "Magmator";
    }

    @LuaFunction(mainThread = true)
    public final double getEnergy() {
        return blockEntity.getEnergy().getEnergyStored();
    }

    @LuaFunction(mainThread = true)
    public final double getMaxEnergy() {
        return blockEntity.getEnergy().getMaxEnergyStored();
    }

    @LuaFunction(mainThread = true)
    public final boolean isBurning() {
        return blockEntity.isBurning();
    }

    @LuaFunction(mainThread = true)
    public final long getTankCapacity() {
        return blockEntity.getTank().getCapacity();
    }

    @LuaFunction(mainThread = true)
    public final long getFluidInTank() {
        return blockEntity.getTank().getFluidAmount();
    }
}
