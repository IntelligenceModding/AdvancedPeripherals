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
package de.srendi.advancedperipherals.common.smartglasses.modules.nightvision;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesAccess;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModuleFunctions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class NightVisionModule implements IModule {

    public boolean nightVisionEnabled = true;

    public NightVisionModule() {

    }

    @Override
    public ResourceLocation getName() {
        return AdvancedPeripherals.getRL("night_vision");
    }

    @Override
    @Nullable public IModuleFunctions getFunctions(SmartGlassesAccess smartGlassesAccess) {
        return new NightVisionFunctions(this);
    }

    @Override
    public void onUnequipped(SmartGlassesAccess smartGlassesAccess) {
        if (smartGlassesAccess.getEntity() != null) {
            if (smartGlassesAccess.getEntity() instanceof Player player) {
                player.removeEffect(MobEffects.NIGHT_VISION);
            }
        }

    }

    public boolean isNightVisionEnabled() {
        return nightVisionEnabled;
    }

    public void enableNightVision(boolean enable) {
        nightVisionEnabled = enable;
    }
}
