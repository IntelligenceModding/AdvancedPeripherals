package de.srendi.advancedperipherals.common.events;

import dan200.computercraft.api.peripheral.IComputerAccess;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.blocks.tileentity.ChatBoxTileEntity;
import de.srendi.advancedperipherals.common.blocks.tileentity.TileEntityList;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AdvancedPeripherals.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Events {

    @SubscribeEvent
    public static void onChat(ServerChatEvent event) {
        for (TileEntity tileEntity : TileEntityList.get(event.getPlayer().getServerWorld()).getTileEntitys(event.getPlayer().getServerWorld())) {
            if (tileEntity instanceof ChatBoxTileEntity) {
                if (AdvancedPeripheralsConfig.enableChatBox) {
                    if (event.getMessage().startsWith("$")) {
                        event.setCanceled(true);
                        ChatBoxTileEntity entity = (ChatBoxTileEntity) tileEntity;
                        for (IComputerAccess computer : entity.getConnectedComputers()) {
                            computer.queueEvent("chat", event.getUsername(), event.getMessage().replace("$", ""));
                        }
                        return;
                    }
                    ChatBoxTileEntity entity = (ChatBoxTileEntity) tileEntity;
                    for (IComputerAccess computer : entity.getConnectedComputers()) {
                        computer.queueEvent("chat", event.getUsername(), event.getMessage());
                    }
                }
            }
        }
    }

}