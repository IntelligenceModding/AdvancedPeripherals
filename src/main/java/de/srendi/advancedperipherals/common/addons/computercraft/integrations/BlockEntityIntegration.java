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

import dan200.computercraft.api.peripheral.IPeripheral;
import de.srendi.advancedperipherals.lib.integrations.IPeripheralIntegration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.Predicate;

public class BlockEntityIntegration implements IPeripheralIntegration {

    private static final int DEFAULT_PRIORITY = 50;

    private final Function<BlockEntity, ? extends IPeripheral> build;
    private final Predicate<BlockEntity> predicate;
    private final int priority;

    public BlockEntityIntegration(Function<BlockEntity, ? extends IPeripheral> build, Predicate<BlockEntity> predicate,
            int priority) {
        this.build = build;
        this.predicate = predicate;
        this.priority = priority;
    }

    public BlockEntityIntegration(Function<BlockEntity, ? extends IPeripheral> build,
            Predicate<BlockEntity> predicate) {
        this(build, predicate, DEFAULT_PRIORITY);
    }

    @Override
    public boolean isSuitable(@NotNull Level level, @NotNull BlockPos blockPos, @NotNull Direction direction) {
        BlockEntity te = level.getBlockEntity(blockPos);
        if (te == null)
            return false;
        return predicate.test(te);
    }

    @Override
    public @NotNull IPeripheral buildPeripheral(@NotNull Level level, @NotNull BlockPos blockPos,
            @NotNull Direction direction) {
        BlockEntity te = level.getBlockEntity(blockPos);
        if (te == null)
            throw new IllegalArgumentException("This should not happen");
        return build.apply(te);
    }

    @Override
    public int getPriority() {
        return priority;
    }
}
