package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.pocket.IPocketAccess;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.common.addons.computercraft.base.BasePeripheral;
import de.srendi.advancedperipherals.common.addons.computercraft.base.OperationPeripheral;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralTileEntity;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.UUID;

public class ChatBoxPeripheral extends OperationPeripheral {

    private final static String SEND_MESSAGE_OPERATION = "sendMessage";

    public ChatBoxPeripheral(String type, PeripheralTileEntity<?> tileEntity) {
        super(type, tileEntity);
    }

    public ChatBoxPeripheral(String type, ITurtleAccess turtle, TurtleSide side) {
        super(type, turtle, side);
    }

    public ChatBoxPeripheral(String type, IPocketAccess pocket) {
        super(type, pocket);
    }

    @Override
    protected int getRawCooldown(String name) {
        if (name.equals(SEND_MESSAGE_OPERATION))
            return AdvancedPeripheralsConfig.chatBoxCooldown;
        return 0;
    }

    @Override
    public boolean isEnabled() {
        return AdvancedPeripheralsConfig.enableChatBox;
    }

    @LuaFunction(mainThread = true)
    public final void sendMessage(Object message) throws LuaException {
        if (isOnCooldown(SEND_MESSAGE_OPERATION))
            throw new LuaException("You are sending messages too often. You can modify the cooldown in the config.");
        for (ServerPlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
            player.sendMessage(new StringTextComponent("" + message), UUID.randomUUID());
        }
        trackOperation(SEND_MESSAGE_OPERATION);
    }

    @LuaFunction(mainThread = true)
    public final void sendMessageToPlayer(Object message, String playerName) throws LuaException {
        if (isOnCooldown(SEND_MESSAGE_OPERATION))
            throw new LuaException("You are sending messages too often. You can modify the cooldown in the config.");
        for (ServerPlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
            if (player.getName().getString().equals(playerName))
                player.sendMessage(new StringTextComponent("" + message), UUID.randomUUID());
        }
        trackOperation(SEND_MESSAGE_OPERATION);
    }

}
