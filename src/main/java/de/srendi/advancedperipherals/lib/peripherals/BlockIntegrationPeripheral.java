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
package de.srendi.advancedperipherals.lib.peripherals;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public abstract class BlockIntegrationPeripheral<T extends Block> extends IntegrationPeripheral {

    protected final Level world;
    protected final BlockPos pos;

    protected BlockIntegrationPeripheral(Level world, BlockPos pos) {
        super();
        this.world = world;
        this.pos = pos;
    }

    public Block getBlock() {
        return world.getBlockState(pos).getBlock();
    }

    @Nullable @Override
    public Object getTarget() {
        return world.getBlockState(pos).getBlock();
    }
}
