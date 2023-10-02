package de.srendi.advancedperipherals.common.addons.botania;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.lib.peripherals.BlockEntityIntegrationPeripheral;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import vazkii.botania.api.block_entity.GeneratingFlowerBlockEntity;

public class ManaFlowerIntegration extends BlockEntityIntegrationPeripheral<GeneratingFlowerBlockEntity> {

    public ManaFlowerIntegration(BlockEntity entity) {
        super(entity);
    }

    @NotNull
    @Override
    public String getType() {
        return "mana_flower";
    }

    @LuaFunction(mainThread = true)
    public final boolean isFloating() {
        return blockEntity.isFloating();
    }

    @LuaFunction(mainThread = true)
    public final int getMaxMana() {
        return blockEntity.getMaxMana();
    }

    @LuaFunction(mainThread = true)
    public final int getMana() {
        return blockEntity.getMana();
    }

    @LuaFunction(mainThread = true)
    public final boolean isOnEnchantedSoil() {
        return blockEntity.overgrowth;
    }
}
