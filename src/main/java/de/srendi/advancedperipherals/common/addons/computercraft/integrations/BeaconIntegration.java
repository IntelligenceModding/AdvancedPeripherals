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
package de.srendi.advancedperipherals.common.addons.computercraft.integrations;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.lib.peripherals.BlockEntityIntegrationPeripheral;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

public class BeaconIntegration extends BlockEntityIntegrationPeripheral<BeaconBlockEntity> {

    public BeaconIntegration(BlockEntity entity) {
        super(entity);
    }

    @Override
    public @NotNull String getType() {
        return "beacon";
    }

    @LuaFunction(mainThread = true)
    public final int getLevel() {
        return blockEntity.levels;
    }

    @LuaFunction(mainThread = true)
    public final String getPrimaryEffect() {
        return blockEntity.primaryPower == null ? "none" : blockEntity.primaryPower.getDescriptionId();
    }

    @LuaFunction(mainThread = true)
    public final String getSecondaryEffect() {
        return blockEntity.secondaryPower == null ? "none" : blockEntity.secondaryPower.getDescriptionId();
    }

}
