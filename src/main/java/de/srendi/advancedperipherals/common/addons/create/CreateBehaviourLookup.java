package de.srendi.advancedperipherals.common.addons.create;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import dan200.computercraft.shared.peripheral.generic.ComponentLookup;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

public record CreateBehaviourLookup<T extends BlockEntityBehaviour>(BehaviourType<T> behaviourType, BiFunction<T, SmartBlockEntity, ?> mapper) implements ComponentLookup {
    @Override
    @Nullable
    public Object find(ServerLevel level, BlockPos pos, BlockState state, BlockEntity be, Direction side) {
        if (be instanceof SmartBlockEntity smartBe) {
            T behaviour = smartBe.getBehaviour(this.behaviourType);
            if (behaviour != null) {
                if (mapper != null) {
                    return mapper.apply(behaviour, smartBe);
                }
                return behaviour;
            }
        }
        return null;
    }
}
