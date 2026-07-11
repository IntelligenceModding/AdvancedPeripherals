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

public record CreateBehaviourLookup(BehaviourType<?> behaviourType) implements ComponentLookup {
    @Override
    @Nullable
    public Object find(ServerLevel level, BlockPos pos, BlockState state, BlockEntity be, Direction side) {
        System.out.println("lookup behaviour " + this.behaviourType + " on " + state + " " + be);
        if (be instanceof SmartBlockEntity smartBe) {
            BlockEntityBehaviour behaviour = smartBe.getBehaviour(this.behaviourType);
            System.out.println(behaviour);
            if (behaviour != null) {
                return behaviour;
            }
        }
        return null;
    }
}
