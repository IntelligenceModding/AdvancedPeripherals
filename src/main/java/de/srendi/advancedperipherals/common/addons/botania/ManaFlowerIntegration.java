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
package de.srendi.advancedperipherals.common.addons.botania;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.lib.peripherals.BlockEntityIntegrationPeripheral;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import vazkii.botania.api.block_entity.GeneratingFlowerBlockEntity;

public class ManaFlowerIntegration extends BlockEntityIntegrationPeripheral<GeneratingFlowerBlockEntity> {

    public ManaFlowerIntegration(BlockEntity entity) {
        super(entity);
    }

    @NotNull @Override
    public String getType() {
        return "mana_flower";
    }

    @LuaFunction(mainThread = true)
    public final boolean isFloating() {
        return blockEntity.isFloating();
    }

    @LuaFunction(mainThread = true)
    public final int getMaxMana() {
        return blockEntity.getMaxMana();
    }

    @LuaFunction(mainThread = true)
    public final int getMana() {
        return blockEntity.getMana();
    }

    @LuaFunction(mainThread = true)
    public final boolean isOnEnchantedSoil() {
        return blockEntity.overgrowth;
    }
}
