package de.srendi.advancedperipherals.common.addons.botania;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.lib.peripherals.APGenericPeripheral;
import de.srendi.advancedperipherals.lib.peripherals.BlockEntityIntegrationPeripheral;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import vazkii.botania.api.block_entity.GeneratingFlowerBlockEntity;
import vazkii.botania.common.block.block_entity.mana.ManaPoolBlockEntity;

public class ManaFlowerIntegration implements APGenericPeripheral {

    @Override
    public String getPeripheralType() {
        return "manaFlower";
    }

    @LuaFunction(mainThread = true)
    public final boolean isFloating(GeneratingFlowerBlockEntity blockEntity) {
        return blockEntity.isFloating();
    }

    @LuaFunction(mainThread = true)
    public final int getMaxMana(GeneratingFlowerBlockEntity blockEntity) {
        return blockEntity.getMaxMana();
    }

    @LuaFunction(mainThread = true)
    public final int getMana(GeneratingFlowerBlockEntity blockEntity) {
        return blockEntity.getMana();
    }

    @LuaFunction(mainThread = true)
    public final boolean isOnEnchantedSoil(GeneratingFlowerBlockEntity blockEntity) {
        return blockEntity.overgrowth;
    }

    @LuaFunction(mainThread = true)
    public final boolean isFull(GeneratingFlowerBlockEntity blockEntity) {
        return blockEntity.getMana() >= blockEntity.getMaxMana();
    }

    @LuaFunction(mainThread = true)
    public final boolean isEmpty(GeneratingFlowerBlockEntity blockEntity) {
        return blockEntity.getMana() == 0;
    }

}
