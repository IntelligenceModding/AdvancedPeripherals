package de.srendi.advancedperipherals.common.events;


import com.google.common.eventbus.Subscribe;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.computercraft.CCEventManager;
import de.srendi.advancedperipherals.common.blocks.tileentity.ChatBoxTileEntity;
import de.srendi.advancedperipherals.common.blocks.tileentity.TileEntityList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AdvancedPeripherals.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Events {

    @SubscribeEvent
    public static void onChat(ServerChatEvent event) {
        for(TileEntity tileEntity : TileEntityList.get().getTileEntitys(event.getPlayer().getServerWorld())) {
            if(tileEntity instanceof ChatBoxTileEntity) {
                CCEventManager.getInstance().sendEvent(tileEntity, "chatEvent", event.getPlayer().getName().getString(), event.getMessage());
            }
        }
    }
}