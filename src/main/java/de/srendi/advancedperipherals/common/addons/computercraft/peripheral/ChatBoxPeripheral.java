package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralTileEntity;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChatBoxPeripheral extends BasePeripheral {

    private int tick;

    public ChatBoxPeripheral(String type, PeripheralTileEntity tileEntity) {
        super(type, tileEntity);
    }

    public int getTick() {
        return tick;
    }

    public void setTick(int tick) {
        this.tick = tick;
    } //TODO: There is a better way to do that, but this works fine for now.

    @Override
    public boolean equals(@Nullable IPeripheral iPeripheral) {
        return iPeripheral == this;
    }

    @LuaFunction(mainThread = true)
    public final void sendMessage(Object message) throws LuaException {
        if (AdvancedPeripheralsConfig.enableChatBox) {
            if (tick >= AdvancedPeripheralsConfig.chatBoxCooldown) {
                if (!AdvancedPeripherals.playerController.getWorld().isRemote()) {
                    ServerWorld world = (ServerWorld) AdvancedPeripherals.playerController.getWorld();
                    for (ServerPlayerEntity player : world.getServer().getPlayerList().getPlayers()) {
                        player.sendMessage(new StringTextComponent("" + message), UUID.randomUUID());
                    }
                } else {
                    Minecraft.getInstance().player.sendMessage(new StringTextComponent("" + message), UUID.randomUUID());
                }
                tick = 0;
            } else {
                throw new LuaException("You are sending messages too often. You can modify the cooldown in the config.");
            }
        }
    }

    @LuaFunction(mainThread = true)
    public final void sendMessageToPlayer(Object message, String playerName) throws LuaException {
        if (AdvancedPeripheralsConfig.enableChatBox) {
            if (tick >= AdvancedPeripheralsConfig.chatBoxCooldown) {
                if (!AdvancedPeripherals.playerController.getWorld().isRemote()) {
                    ServerWorld world = (ServerWorld) AdvancedPeripherals.playerController.getWorld();
                    for (ServerPlayerEntity player : world.getServer().getPlayerList().getPlayers()) {
                        if (player.getName().getString().equals(playerName)) {
                            player.sendMessage(new StringTextComponent((String) message), UUID.randomUUID());
                        }
                    }
                } else {
                    if (Minecraft.getInstance().player.getName().getString().equals(playerName)) {
                        Minecraft.getInstance().player.sendMessage(new StringTextComponent("" + message), UUID.randomUUID());
                    }
                }
                tick = 0;
            } else {
                throw new LuaException("You are sending messages too often. You can modify the cooldown in the config.");
            }
        }
    }
}
