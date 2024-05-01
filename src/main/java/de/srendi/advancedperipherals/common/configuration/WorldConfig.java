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
package de.srendi.advancedperipherals.common.configuration;

import net.minecraft.FieldsAreNonnullByDefault;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

@FieldsAreNonnullByDefault
public class WorldConfig implements IAPConfig {

    public final ForgeConfigSpec.BooleanValue enableVillagerStructures;
    public final ForgeConfigSpec.BooleanValue givePlayerBookOnJoin;
    public final ForgeConfigSpec.IntValue villagerStructureWeight;
    private final ForgeConfigSpec configSpec;

    public WorldConfig() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.comment("Config to adjust world settings").push("World");

        enableVillagerStructures = builder.comment("Enable the villager structures for the computer scientist.")
                .define("enableVillagerStructures", true);
        givePlayerBookOnJoin = builder.comment("Gives the ap documentation to new players.")
                .define("givePlayerBookOnJoin", true);
        villagerStructureWeight = builder.comment("The weight of the villager structures.")
                .defineInRange("villagerStructureWeight", 10, 0, 16000);

        builder.pop();
        configSpec = builder.build();
    }

    @Override
    public ForgeConfigSpec getConfigSpec() {
        return configSpec;
    }

    @Override
    public String getFileName() {
        return "world";
    }

    @Override
    public ModConfig.Type getType() {
        return ModConfig.Type.COMMON;
    }
    /*
     * [
     */
}
