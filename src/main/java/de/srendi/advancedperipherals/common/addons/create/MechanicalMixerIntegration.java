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
package de.srendi.advancedperipherals.common.addons.create;

import com.simibubi.create.content.kinetics.mixer.MechanicalMixerBlockEntity;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.lib.peripherals.BlockEntityIntegrationPeripheral;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

public class MechanicalMixerIntegration extends BlockEntityIntegrationPeripheral<MechanicalMixerBlockEntity> {

    public MechanicalMixerIntegration(BlockEntity entity) {
        super(entity);
    }

    @NotNull @Override
    public String getType() {
        return "mechanical_mixer";
    }

    @LuaFunction(mainThread = true)
    public final boolean isRunning() {
        return blockEntity.running;
    }

    @LuaFunction(mainThread = true)
    public final boolean hasBasin() {
        if (blockEntity.getLevel() == null)
            return false;
        BlockEntity basinTE = blockEntity.getLevel().getBlockEntity(blockEntity.getBlockPos().below(2));
        return basinTE instanceof BasinBlockEntity;
    }
}
