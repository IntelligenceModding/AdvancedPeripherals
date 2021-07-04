package de.srendi.advancedperipherals.common.addons.computercraft.peripheral.mechanic;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import de.srendi.advancedperipherals.common.util.Pair;
import de.srendi.advancedperipherals.common.util.fakeplayer.FakePlayerProviderTurtle;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;


public class OverpoweredWeakMechanicSoulPeripheral extends WeakMechanicSoulPeripheral {
    public OverpoweredWeakMechanicSoulPeripheral(String type, ITurtleAccess turtle) {
        super(type, turtle);
    }

    @LuaFunction(mainThread = true)
    public MethodResult digBlock(@NotNull IComputerAccess access) {
        Optional<MethodResult> checkResults = turtleChecks();
        if (checkResults.isPresent()) return checkResults.get();
        checkResults = consumeFuelOp(AdvancedPeripheralsConfig.digBlockCost);
        if (checkResults.isPresent()) return checkResults.map(result -> fuelErrorCallback(access, result)).get();

        BlockPos blockPos = turtle.getPosition().relative(turtle.getDirection());

        ItemStack selectedTool = turtle.getInventory().getItem(turtle.getSelectedSlot());
        int previousDamageValue = selectedTool.getDamageValue();

        Pair<Boolean, String> result = FakePlayerProviderTurtle.withPlayer(turtle, turtleFakePlayer -> turtleFakePlayer.digBlock(blockPos, turtle.getDirection().getOpposite()));
        if (!result.getLeft()) {
            return MethodResult.of(null, result.getRight());
        }

        selectedTool = turtle.getInventory().getItem(turtle.getSelectedSlot());
        selectedTool.setDamageValue(previousDamageValue);

        return MethodResult.of(true);
    }

    @NotNull
    @Override
    protected MethodResult fuelErrorCallback(@NotNull IComputerAccess access, MethodResult fuelErrorResult) {
        Pair<MethodResult, TurtleSide> sidePair = getTurtleSide(access);
        if (sidePair.leftPresent())
            return fuelErrorResult;
        turtle.setUpgrade(sidePair.getRight(), null);
        return MethodResult.of(null, "Too much power! Soul is broken ...");
    }
}
