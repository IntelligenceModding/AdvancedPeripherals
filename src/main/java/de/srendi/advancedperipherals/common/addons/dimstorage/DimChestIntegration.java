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
package de.srendi.advancedperipherals.common.addons.dimstorage;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.lib.peripherals.BlockEntityIntegrationPeripheral;
import edivad.dimstorage.api.Frequency;
import edivad.dimstorage.blockentities.BlockEntityDimChest;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class DimChestIntegration extends BlockEntityIntegrationPeripheral<BlockEntityDimChest> {

    protected DimChestIntegration(BlockEntity entity) {
        super(entity);
    }

    @NotNull @Override
    public String getType() {
        return "dimChest";
    }

    @LuaFunction(mainThread = true)
    public final String getOwnerUUID() {
        UUID uuid = blockEntity.getFrequency().getOwnerUUID();
        if (uuid == null)
            return null;
        return uuid.toString();
    }

    @LuaFunction(mainThread = true)
    public final String getOwner() {
        return blockEntity.getFrequency().getOwner();
    }

    @LuaFunction(mainThread = true)
    public final boolean hasOwner() {
        return blockEntity.getFrequency().hasOwner();
    }

    @LuaFunction(mainThread = true)
    public final int getChannel() {
        return blockEntity.getFrequency().getChannel();
    }

    @LuaFunction(mainThread = true)
    public final boolean setChannel(int channel) {
        Frequency fre = blockEntity.getFrequency();
        if (fre.hasOwner())
            return false;
        fre.setChannel(channel);
        blockEntity.setFrequency(fre);
        return true;
    }
}
