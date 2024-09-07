package de.srendi.advancedperipherals.common.addons.botania;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.lib.peripherals.BlockEntityIntegrationPeripheral;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import vazkii.botania.common.block.block_entity.mana.ManaPoolBlockEntity;

public class ManaPoolIntegration extends BlockEntityIntegrationPeripheral<ManaPoolBlockEntity> {

    public ManaPoolIntegration(BlockEntity entity) {
        super(entity);
    }

    @NotNull
    @Override
    public String getType() {
        return "mana_pool";
    }

    @LuaFunction(mainThread = true)
    public final int getMana() {
        return blockEntity.getCurrentMana();
    }

    @LuaFunction(mainThread = true)
    public final int getMaxMana() {
        return blockEntity.getMaxMana();
    }

    @LuaFunction(mainThread = true)
    public final int getManaNeeded() {
        return blockEntity.getAvailableSpaceForMana();
    }

    @LuaFunction(mainThread = true)
    public final boolean isFull() {
        return blockEntity.isFull();
    }

}
