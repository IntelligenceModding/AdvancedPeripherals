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
package de.srendi.advancedperipherals.common.blocks.blockentities;

import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.PlayerDetectorPeripheral;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralBlockEntity;
import de.srendi.advancedperipherals.common.events.Events;
import de.srendi.advancedperipherals.common.setup.APBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class PlayerDetectorEntity extends PeripheralBlockEntity<PlayerDetectorPeripheral> {
    private Long lastConsumedMessage;

    public PlayerDetectorEntity(BlockPos pos, BlockState state) {
        super(APBlockEntityTypes.PLAYER_DETECTOR.get(), pos, state);
        lastConsumedMessage = Events.getLastPlayerMessageID() - 1;
    }

    @NotNull @Override
    protected PlayerDetectorPeripheral createPeripheral() {
        return new PlayerDetectorPeripheral(this);
    }

    @Override
    public <T extends BlockEntity> void handleTick(Level level, BlockState state, BlockEntityType<T> type) {
        lastConsumedMessage = Events.traversePlayerMessages(lastConsumedMessage,
                message -> getConnectedComputers().forEach(computer -> {
                    if (message.eventName().equals("playerChangedDimension")) {
                        computer.queueEvent(message.eventName(), message.playerName(), message.fromDimension(),
                                message.toDimension());
                    } else
                        computer.queueEvent(message.eventName(), message.playerName(), message.fromDimension());
                }));
    }
}
