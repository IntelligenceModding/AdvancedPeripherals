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
package de.srendi.advancedperipherals.common.blocks.base;

import de.srendi.advancedperipherals.common.blocks.blockentities.EnergyDetectorEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class APBlockEntityBlock<T extends BlockEntity> extends BaseBlockEntityBlock {

    private final RegistryObject<BlockEntityType<T>> tileEntity;

    public APBlockEntityBlock(RegistryObject<BlockEntityType<T>> tileEntity, Properties properties,
            boolean belongToTickingEntity) {
        super(belongToTickingEntity, properties);
        this.tileEntity = tileEntity;
    }

    public APBlockEntityBlock(RegistryObject<BlockEntityType<T>> tileEntity, boolean belongToTickingEntity) {
        this(tileEntity, Properties.of(Material.METAL), belongToTickingEntity);
    }

    @Nullable @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return tileEntity != null ? tileEntity.get().create(pos, state) : null;
    }

    @Override
    public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
        super.onNeighborChange(state, level, pos, neighbor);

        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (blockEntity instanceof EnergyDetectorEntity energyDetector)
            energyDetector.invalidateStorages();

    }
}
