package de.srendi.advancedperipherals.common.village;

import dan200.computercraft.shared.ModRegistry;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.setup.APBlocks;
import de.srendi.advancedperipherals.common.setup.APItems;
import de.srendi.advancedperipherals.common.setup.APVillagers;
import de.srendi.advancedperipherals.common.setup.CCRegistration;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.neoforged.neoforge.event.village.WandererTradesEvent;

import static de.srendi.advancedperipherals.common.village.VillagerTrade.TradeBuilder;

@EventBusSubscriber
public class VillagerTrades {
    @SubscribeEvent
    public static void registerWanderingTrade(WandererTradesEvent event) {
        if (APConfig.WORLD_CONFIG.enableWanderingTraderTrades.get()) {
            new TradeBuilder(event, VillagerTrade.Type.ITEM_FOR_EMERALD)
                .withItem(APBlocks.PERIPHERAL_CASING.get())
                .withMaxUses(8)
                .build();
            new TradeBuilder(event, VillagerTrade.Type.ITEM_FOR_EMERALD)
                .withItem(ModRegistry.Blocks.TURTLE_ADVANCED.get())
                .withEmeralds(2)
                .withMaxUses(8)
                .build();
            new TradeBuilder(event, VillagerTrade.Type.ITEM_FOR_EMERALD)
                .withItem(APItems.SMART_GLASSES.get())
                .withEmeralds(32)
                .withMaxUses(1)
                .build();
        }
    }

    @SubscribeEvent
    public static void registerVillagerTrades(VillagerTradesEvent event) {
        if (event.getType() == APVillagers.COMPUTER_SCIENTIST.get()) {
            new TradeBuilder(event, VillagerTrade.Type.ITEM_FOR_EMERALD)
                .withItem(APBlocks.PERIPHERAL_CASING.get())
                .withXp(1)
                .build();
            new TradeBuilder(event, VillagerTrade.Type.ITEM_FOR_EMERALD)
                .withItem(APBlocks.CHAT_BOX.get())
                .withEmeralds(2)
                .build();
            new TradeBuilder(event, VillagerTrade.Type.ITEM_FOR_EMERALD)
                .withItem(ModRegistry.Blocks.MONITOR_NORMAL.get())
                .withItemAmount(2)
                .withEmeralds(2)
                .build();
            new TradeBuilder(event, VillagerTrade.Type.EMERALD_FOR_ITEM)
                .withItem(APBlocks.PERIPHERAL_CASING.get())
                .withMaxUses(5)
                .build();

            new TradeBuilder(event, VillagerTrade.Type.ITEM_FOR_EMERALD)
                .withItem(APBlocks.INVENTORY_MANAGER.get())
                .withEmeralds(2)
                .withProfessionLevel(2)
                .withXp(4)
                .build();
            new TradeBuilder(event, VillagerTrade.Type.ITEM_FOR_EMERALD)
                .withItem(ModRegistry.Blocks.COMPUTER_ADVANCED.get())
                .withEmeralds(3)
                .withProfessionLevel(2)
                .withXp(4)
                .build();

            new TradeBuilder(event, VillagerTrade.Type.ITEM_FOR_EMERALD)
                .withTurtleUpgrade(false, CCRegistration.ID.Turtle.ENVIRONMENT)
                .withEmeralds(4)
                .withProfessionLevel(3)
                .withMaxUses(4)
                .withXp(5)
                .build();
            new TradeBuilder(event, VillagerTrade.Type.ITEM_FOR_EMERALD)
                .withPocketUpgrade(true, CCRegistration.ID.Pocket.PLAYER)
                .withEmeralds(4)
                .withProfessionLevel(3)
                .withMaxUses(4)
                .withXp(7)
                .build();
            new TradeBuilder(event, VillagerTrade.Type.EMERALD_FOR_ITEM)
                .withItem(APItems.CHUNK_CONTROLLER.get())
                .withEmeralds(5)
                .withProfessionLevel(3)
                .withMaxUses(6)
                .withXp(8)
                .build();
            new TradeBuilder(event, VillagerTrade.Type.ITEM_FOR_EMERALD)
                .withItem(APItems.COMPUTER_TOOL.get())
                .withEmeralds(1)
                .withProfessionLevel(3)
                .withMaxUses(1)
                .withXp(16)
                .build();
            new TradeBuilder(event, VillagerTrade.Type.ITEM_FOR_EMERALD)
                .withItem(ModRegistry.Blocks.MONITOR_ADVANCED.get())
                .withItemAmount(4)
                .withEmeralds(6)
                .withProfessionLevel(3)
                .withXp(7)
                .build();

            new TradeBuilder(event, VillagerTrade.Type.ITEM_FOR_EMERALD)
                .withItem(APBlocks.ENERGY_DETECTOR.get())
                .withEmeralds(4)
                .withProfessionLevel(4)
                .withXp(6)
                .build();
            new TradeBuilder(event, VillagerTrade.Type.ITEM_FOR_EMERALD)
                .withItem(APBlocks.FLUID_DETECTOR.get())
                .withEmeralds(4)
                .withProfessionLevel(4)
                .withXp(6)
                .build();
            new TradeBuilder(event, VillagerTrade.Type.ITEM_FOR_EMERALD)
                .withItem(APBlocks.GAS_DETECTOR.get())
                .withEmeralds(4)
                .withProfessionLevel(4)
                .withXp(6)
                .build();
            new TradeBuilder(event, VillagerTrade.Type.ITEM_FOR_EMERALD)
                .withItem(ModRegistry.Blocks.COMPUTER_ADVANCED.get())
                .withEmeralds(4)
                .withProfessionLevel(4)
                .withMaxUses(3)
                .withXp(5)
                    .build();
            new TradeBuilder(event, VillagerTrade.Type.EMERALD_FOR_ITEM)
                .withItem(APItems.MEMORY_CARD.get())
                .withEmeralds(2)
                .withProfessionLevel(4)
                .withXp(4)
                .build();
            new TradeBuilder(event, VillagerTrade.Type.ITEM_FOR_EMERALD)
                .withTurtleUpgrade(true, CCRegistration.ID.Turtle.CHUNKY)
                .withEmeralds(8)
                .withProfessionLevel(4)
                .withXp(7)
                .build();
            new TradeBuilder(event, VillagerTrade.Type.ITEM_FOR_EMERALD)
                .withItem(APItems.CHUNK_CONTROLLER.get())
                .withEmeralds(6)
                .withProfessionLevel(4)
                .withMaxUses(6)
                .withXp(6)
                .build();

            new TradeBuilder(event, VillagerTrade.Type.ITEM_FOR_EMERALD)
                .withItem(APBlocks.ME_BRIDGE.get())
                .withItemAmount(2)
                .withEmeralds(4)
                .withProfessionLevel(5)
                .withXp(4)
                .build();
            new TradeBuilder(event, VillagerTrade.Type.ITEM_FOR_EMERALD)
                .withItem(ModRegistry.Blocks.SPEAKER.get())
                .withEmeralds(3)
                .withProfessionLevel(5)
                .withXp(6)
                .build();
            new TradeBuilder(event, VillagerTrade.Type.ITEM_FOR_EMERALD)
                .withItem(ModRegistry.Blocks.WIRELESS_MODEM_ADVANCED.get())
                .withEmeralds(4)
                .withProfessionLevel(5)
                .withXp(8)
                .build();
            new TradeBuilder(event, VillagerTrade.Type.EMERALD_FOR_ITEM)
                .withItem(APItems.SMART_GLASSES_NETHERITE.get())
                .withEmeralds(64)
                .withProfessionLevel(5)
                .withMaxUses(1)
                .withXp(16)
                .build();

        }
    }

    @SubscribeEvent
    public static void onVillagerDrop(LivingDropsEvent event) {
        if (!(event.getEntity() instanceof Villager villager)) {
            return;
        }
        if (villager.getVillagerData().getProfession() == APVillagers.COMPUTER_SCIENTIST.get()) {
            if (villager.level().random.nextDouble() < 0.01) {
                event.getDrops().add(new ItemEntity(villager.level(), villager.getX(), villager.getEyeY(), villager.getZ(), new ItemStack(APItems.SMART_GLASSES.get())));
            }
        }
    }
}
