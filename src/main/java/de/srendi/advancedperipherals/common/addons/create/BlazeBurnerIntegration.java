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

import com.simibubi.create.content.processing.burner.BlazeBurnerBlockEntity;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.lib.peripherals.BlockEntityIntegrationPeripheral;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class BlazeBurnerIntegration extends BlockEntityIntegrationPeripheral<BlazeBurnerBlockEntity> {

    public BlazeBurnerIntegration(BlockEntity entity) {
        super(entity);
    }

    @NotNull @Override
    public String getType() {
        return "blaze_burner";
    }

    @LuaFunction(mainThread = true)
    public final Map<String, Object> getInfo() {
        Map<String, Object> data = new HashMap<>();
        data.put("fuelType", blockEntity.getActiveFuel().toString().toLowerCase());
        data.put("heatLevel", blockEntity.getHeatLevelFromBlock().getSerializedName());
        data.put("remainingBurnTime", blockEntity.getRemainingBurnTime());
        data.put("isCreative", blockEntity.isCreative());
        return data;
    }

}
