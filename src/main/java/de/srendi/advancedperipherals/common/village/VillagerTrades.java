package de.srendi.advancedperipherals.common.village;

import dan200.computercraft.shared.Registry;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.setup.APBlocks;
import de.srendi.advancedperipherals.common.setup.APItems;
import de.srendi.advancedperipherals.common.setup.APVillagers;
import de.srendi.advancedperipherals.common.setup.CCRegistration;
import de.srendi.advancedperipherals.common.util.ItemUtil;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = AdvancedPeripherals.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class VillagerTrades {

    @SubscribeEvent
    public static void registerWanderingTrade(WandererTradesEvent event) {
        event.getGenericTrades().add(new VillagerTrade(VillagerTrade.Type.ItemForEmerald).setItem(APBlocks.PERIPHERAL_CASING.get()).setEmeraldPrice(1).setMaxUses(16).setXp(3));

        event.getRareTrades().add(new VillagerTrade(VillagerTrade.Type.EmeraldForItem).setItem(Registry.ModBlocks.TURTLE_ADVANCED.get()).setEmeraldPrice(2).setMaxUses(4).setXp(20).setItemAmount(1));
    }

    @SubscribeEvent
    public static void registerVillagerTrades(VillagerTradesEvent event) {
        Int2ObjectMap<List<net.minecraft.world.entity.npc.VillagerTrades.ItemListing>> trades = event.getTrades();
        if (event.getType() == APVillagers.COMPUTER_SCIENTIST.get()) {

            trades.get(1).add(new VillagerTrade(VillagerTrade.Type.ItemForEmerald).setItem(APBlocks.PERIPHERAL_CASING.get()).setEmeraldPrice(2).setMaxUses(10).setXp(4));
            trades.get(1).add(new VillagerTrade(VillagerTrade.Type.ItemForEmerald).setItem(APBlocks.CHAT_BOX.get()).setEmeraldPrice(2).setMaxUses(8).setXp(4));
            trades.get(1).add(new VillagerTrade(VillagerTrade.Type.ItemForEmerald).setItem(Registry.ModBlocks.COMPUTER_NORMAL.get()).setEmeraldPrice(1).setMaxUses(8).setXp(4));
            trades.get(1).add(new VillagerTrade(VillagerTrade.Type.EmeraldForItem).setItem(APBlocks.PERIPHERAL_CASING.get()).setEmeraldPrice(1).setMaxUses(8).setXp(2));

            trades.get(2).add(new VillagerTrade(VillagerTrade.Type.ItemForEmerald).setItem(APBlocks.INVENTORY_MANAGER.get()).setEmeraldPrice(2).setMaxUses(2).setXp(8));
            trades.get(2).add(new VillagerTrade(VillagerTrade.Type.ItemForEmerald).setItem(APBlocks.REDSTONE_INTEGRATOR.get()).setEmeraldPrice(2).setMaxUses(2).setXp(8));
            trades.get(2).add(new VillagerTrade(VillagerTrade.Type.ItemForEmerald).setItem(Registry.ModBlocks.COMPUTER_ADVANCED.get()).setEmeraldPrice(2).setMaxUses(3).setXp(10));
            trades.get(2).add(new VillagerTrade(VillagerTrade.Type.ItemForEmerald).setItem(Registry.ModBlocks.MONITOR_NORMAL.get()).setEmeraldPrice(1).setMaxUses(8).setXp(6).setItemAmount(8));

            trades.get(3).add(new VillagerTrade(VillagerTrade.Type.ItemForEmerald).setItem(ItemUtil.makeTurtle(ItemUtil.TURTLE_NORMAL, CCRegistration.ID.ENVIRONMENT_TURTLE.toString())).setEmeraldPrice(3).setMaxUses(4).setXp(10));
            trades.get(3).add(new VillagerTrade(VillagerTrade.Type.ItemForEmerald).setItem(ItemUtil.makePocket(ItemUtil.POCKET_NORMAL, CCRegistration.ID.PLAYER_POCKET.toString())).setEmeraldPrice(2).setMaxUses(4).setXp(10));
            trades.get(3).add(new VillagerTrade(VillagerTrade.Type.EmeraldForItem).setItem(APItems.CHUNK_CONTROLLER.get()).setEmeraldPrice(1).setMaxUses(6).setXp(8));
            trades.get(3).add(new VillagerTrade(VillagerTrade.Type.ItemForEmerald).setItem(APItems.COMPUTER_TOOL.get()).setEmeraldPrice(1).setMaxUses(4).setXp(8));
            trades.get(3).add(new VillagerTrade(VillagerTrade.Type.ItemForEmerald).setItem(Registry.ModBlocks.MONITOR_ADVANCED.get()).setEmeraldPrice(6).setMaxUses(8).setXp(10).setItemAmount(16));

            trades.get(4).add(new VillagerTrade(VillagerTrade.Type.ItemForEmerald).setItem(APBlocks.ENERGY_DETECTOR.get()).setEmeraldPrice(3).setMaxUses(8).setXp(12));
            trades.get(4).add(new VillagerTrade(VillagerTrade.Type.EmeraldForItem).setItem(Registry.ModBlocks.COMPUTER_ADVANCED.get()).setEmeraldPrice(2).setMaxUses(8).setXp(12));
            trades.get(4).add(new VillagerTrade(VillagerTrade.Type.ItemForEmerald).setItem(APItems.MEMORY_CARD.get()).setEmeraldPrice(1).setMaxUses(8).setXp(18));
            trades.get(4).add(new VillagerTrade(VillagerTrade.Type.EmeraldForItem).setItem(Registry.ModItems.PRINTED_PAGE.get()).setEmeraldPrice(1).setMaxUses(10).setXp(16).setItemAmount(4));
            trades.get(4).add(new VillagerTrade(VillagerTrade.Type.ItemForEmerald).setItem(ItemUtil.makeTurtle(ItemUtil.TURTLE_ADVANCED, AdvancedPeripherals.MOD_ID + ":chunky_turtle")).setEmeraldPrice(8).setMaxUses(6).setXp(18));

            trades.get(5).add(new VillagerTrade(VillagerTrade.Type.ItemForEmerald).setItem(APBlocks.AR_CONTROLLER.get()).setEmeraldPrice(3).setMaxUses(6).setXp(30));
            trades.get(5).add(new VillagerTrade(VillagerTrade.Type.ItemForEmerald).setItem(APItems.SMART_GLASSES.get()).setEmeraldPrice(4).setMaxUses(8).setXp(30));
            trades.get(5).add(new VillagerTrade(VillagerTrade.Type.EmeraldForItem).setItem(Registry.ModBlocks.WIRELESS_MODEM_ADVANCED.get()).setEmeraldPrice(2).setMaxUses(14).setXp(30));

        }
    }

}
