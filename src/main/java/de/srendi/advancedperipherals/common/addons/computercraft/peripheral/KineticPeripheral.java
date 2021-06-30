package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.turtle.ITurtleAccess;
import de.srendi.advancedperipherals.common.addons.computercraft.base.BasePeripheral;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Hand;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayer;

import java.util.Locale;

public class KineticPeripheral extends BasePeripheral {

    public KineticPeripheral(String type, ITurtleAccess turtle) {
        super(type, turtle);
    }

    @Override
    public boolean isEnabled() {
        return AdvancedPeripheralsConfig.enableChunkyTurtle;
    }

    @LuaFunction
    public final MethodResult use(String hand) throws LuaException {
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

        hand = hand == null ? "main" : hand.toLowerCase(Locale.ENGLISH);
        final Hand handE;
        switch (hand) {
            case "main":
            case "mainhand":
                handE = Hand.MAIN_HAND;
                break;
            case "off":
            case "offhand":
                handE = Hand.OFF_HAND;
                break;
            default:
                throw new LuaException("Unknown hand '" + hand + "', expected 'main' or 'off'");
        }

        FakePlayer fakePlayer = new FakePlayer(server.overworld(), turtle.getOwningPlayer());

        fakePlayer.remove();

        return MethodResult.of(true);
    }

}
