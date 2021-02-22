package de.srendi.advancedperipherals.common.setup;

import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.shared.TurtleUpgrades;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.computercraft.turtles.*;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AdvancedPeripherals.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Turtles {

    private static TurtleChatBox chatBox;

    public static void register() {
        chatBox = new TurtleChatBox();
        ComputerCraftAPI.registerTurtleUpgrade(chatBox);
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        register();
        TurtleUpgrades.enable(chatBox);
    }

}
