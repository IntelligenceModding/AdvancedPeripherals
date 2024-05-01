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
package de.srendi.advancedperipherals.common.addons.computercraft.owner;

import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.lib.peripherals.IPeripheralTileEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.jetbrains.annotations.NotNull;

public class TileEntityFuelAbility<T extends BlockEntity & IPeripheralTileEntity>
        extends
            FuelAbility<BlockEntityPeripheralOwner<T>> {

    public TileEntityFuelAbility(@NotNull BlockEntityPeripheralOwner<T> owner) {
        super(owner);
    }

    @Override
    protected boolean consumeFuel(int count) {
        return owner.tileEntity.getCapability(ForgeCapabilities.ENERGY).map(storage -> {
            int energyCount = count * APConfig.METAPHYSICS_CONFIG.energyToFuelRate.get();
            int extractedCount = storage.extractEnergy(energyCount, true);
            if (extractedCount == energyCount) {
                storage.extractEnergy(energyCount, false);
                return true;
            }
            return false;
        }).orElse(false);
    }

    @Override
    protected int getMaxFuelConsumptionRate() {
        return DEFAULT_FUEL_CONSUMING_RATE;
    }

    @Override
    public boolean isFuelConsumptionDisable() {
        return !APConfig.PERIPHERALS_CONFIG.enablePoweredPeripherals.get();
    }

    @Override
    public int getFuelCount() {
        return owner.tileEntity.getCapability(ForgeCapabilities.ENERGY)
                .map(storage -> storage.getEnergyStored() / APConfig.METAPHYSICS_CONFIG.energyToFuelRate.get())
                .orElse(0);
    }

    @Override
    public int getFuelMaxCount() {
        return owner.tileEntity.getCapability(ForgeCapabilities.ENERGY)
                .map(storage -> storage.getMaxEnergyStored() / APConfig.METAPHYSICS_CONFIG.energyToFuelRate.get())
                .orElse(0);
    }

    @Override
    public void addFuel(int count) {
        owner.tileEntity.getCapability(ForgeCapabilities.ENERGY).ifPresent(storage -> {
            int energyCount = count * APConfig.METAPHYSICS_CONFIG.energyToFuelRate.get();
            storage.receiveEnergy(energyCount, false);
        });
    }
}
