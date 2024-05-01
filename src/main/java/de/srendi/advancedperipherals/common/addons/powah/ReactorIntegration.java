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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import owmii.powah.block.reactor.ReactorPartTile;

public class ReactorIntegration extends BlockEntityIntegrationPeripheral<ReactorPartTile> {
    protected ReactorIntegration(BlockEntity entity) {
        super(entity);
    }

    @Override
    @NotNull public String getType() {
        return "uraninite_reactor";
    }

    @LuaFunction(mainThread = true)
    public final String getName() {
        return "Uraninite Reactor";
    }

    @LuaFunction(mainThread = true)
    public final boolean isRunning() {
        if (blockEntity.core().isEmpty())
            return false;
        return blockEntity.core().get().isRunning();
    }

    @LuaFunction(mainThread = true)
    public final double getFuel() {
        if (blockEntity.core().isEmpty())
            return 0.0d;
        return blockEntity.core().get().fuel.perCent();
    }

    @LuaFunction(mainThread = true)
    public final double getCarbon() {
        if (blockEntity.core().isEmpty())
            return 0.0d;
        return blockEntity.core().get().carbon.perCent();
    }

    @LuaFunction(mainThread = true)
    public final double getRedstone() {
        if (blockEntity.core().isEmpty())
            return 0.0d;
        return blockEntity.core().get().redstone.perCent();
    }

    @LuaFunction(mainThread = true)
    public final double getEnergy() {
        if (blockEntity.core().isEmpty())
            return 0.0d;
        return blockEntity.core().get().getEnergy().getEnergyStored();
    }

    @LuaFunction(mainThread = true)
    public final double getMaxEnergy() {
        if (blockEntity.core().isEmpty())
            return 0.0d;
        return blockEntity.core().get().getEnergy().getMaxEnergyStored();
    }

    @LuaFunction(mainThread = true)
    public final double getTemperature() {
        if (blockEntity.core().isEmpty())
            return 0.0d;
        return blockEntity.core().get().temp.perCent();
    }

    @LuaFunction(mainThread = true)
    public final ItemStack getInventoryUraninite() {
        if (blockEntity.core().isEmpty())
            return ItemStack.EMPTY;
        return blockEntity.core().get().getInventory().getStackInSlot(1);
    }

    @LuaFunction(mainThread = true)
    public final ItemStack getInventoryRedstone() {
        if (blockEntity.core().isEmpty())
            return ItemStack.EMPTY;
        return blockEntity.core().get().getInventory().getStackInSlot(3);
    }

    @LuaFunction(mainThread = true)
    public final ItemStack getInventoryCarbon() {
        if (blockEntity.core().isEmpty())
            return ItemStack.EMPTY;
        return blockEntity.core().get().getInventory().getStackInSlot(2);
    }
}
