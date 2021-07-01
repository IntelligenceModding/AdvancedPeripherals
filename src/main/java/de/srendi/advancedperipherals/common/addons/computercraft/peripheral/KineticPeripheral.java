package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import com.mojang.datafixers.util.Pair;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.turtle.ITurtleAccess;
import de.srendi.advancedperipherals.common.addons.computercraft.base.BasePeripheral;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import de.srendi.advancedperipherals.common.util.FakePlayerProviderTurtle;
import de.srendi.advancedperipherals.common.util.PlethoraFakePlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class KineticPeripheral extends BasePeripheral {

    public KineticPeripheral(String type, ITurtleAccess turtle) {
        super(type, turtle);
    }

    @Override
    public boolean isEnabled() {
        return AdvancedPeripheralsConfig.enableChunkyTurtle;
    }

    @LuaFunction
    public final MethodResult digWithTool() {
        if (turtle == null) {
            return MethodResult.of(null, "Well, you can use it only from turtle now!");
        }
        if (turtle.getOwningPlayer() == null) {
            return MethodResult.of(null, "Well, turtle should have owned player!");
        }
        MinecraftServer server = getWorld().getServer();
        if (server == null) {
            return MethodResult.of(null, "Problem with server finding ...");
        }

        PlethoraFakePlayer fakePlayer = FakePlayerProviderTurtle.getPlayer(turtle, turtle.getOwningPlayer());
        BlockPos blockPos = turtle.getPosition().relative(turtle.getDirection());

        FakePlayerProviderTurtle.load(fakePlayer, turtle, turtle.getDirection());

        Pair<Boolean, String> result = fakePlayer.dig(blockPos, turtle.getDirection().getOpposite());
        if (!result.getFirst()) {
            return MethodResult.of(null, result.getSecond());
        }

        return MethodResult.of(true);
    }

}
