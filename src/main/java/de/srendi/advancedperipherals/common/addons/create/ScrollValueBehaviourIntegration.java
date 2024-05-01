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

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.lib.peripherals.BlockEntityIntegrationPeripheral;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

/**
 * Integration for kinetic tile entities with scroll value behaviours like the
 * speed controller or the creative motor
 */
public class ScrollValueBehaviourIntegration extends BlockEntityIntegrationPeripheral<KineticBlockEntity> {

    public ScrollValueBehaviourIntegration(BlockEntity entity) {
        super(entity);
    }

    @NotNull @Override
    public String getType() {
        return "scroll_behaviour_entity";
    }

    @LuaFunction(mainThread = true)
    public final int getTargetSpeed() {
        ScrollValueBehaviour scrollBehaviour = blockEntity.getBehaviour(ScrollValueBehaviour.TYPE);
        if (scrollBehaviour == null)
            return 0;
        return scrollBehaviour.getValue();
    }

    @LuaFunction(mainThread = true)
    public final boolean setTargetSpeed(int speed) {
        ScrollValueBehaviour scrollBehaviour = blockEntity.getBehaviour(ScrollValueBehaviour.TYPE);
        if (scrollBehaviour == null)
            return false;
        scrollBehaviour.setValue(speed);
        return true;
    }
}
