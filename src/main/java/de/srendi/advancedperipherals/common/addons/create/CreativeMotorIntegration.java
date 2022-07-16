package de.srendi.advancedperipherals.common.addons.create;

import com.simibubi.create.content.contraptions.components.motor.CreativeMotorTileEntity;
import com.simibubi.create.foundation.tileEntity.behaviour.scrollvalue.ScrollValueBehaviour;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.lib.peripherals.BlockEntityIntegrationPeripheral;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

public class CreativeMotorIntegration extends BlockEntityIntegrationPeripheral<CreativeMotorTileEntity> {

    public CreativeMotorIntegration(BlockEntity entity) {
        super(entity);
    }

    @NotNull
    @Override
    public String getType() {
        return "creativeMotor";
    }

    @LuaFunction(mainThread = true)
    public final int getTargetSpeed() {
        ScrollValueBehaviour scrollBehaviour = blockEntity.getBehaviour(ScrollValueBehaviour.TYPE);
        return scrollBehaviour.getValue();
    }

    @LuaFunction(mainThread = true)
    public final boolean setTargetSpeed(int speed) {
        ScrollValueBehaviour scrollBehaviour = blockEntity.getBehaviour(ScrollValueBehaviour.TYPE);
        scrollBehaviour.setValue(speed);
        return true;
    }
}
