package de.srendi.advancedperipherals.common.village;

import dan200.computercraft.api.pocket.IPocketUpgrade;
import dan200.computercraft.api.turtle.ITurtleUpgrade;
import dan200.computercraft.api.upgrades.UpgradeData;
import dan200.computercraft.shared.ModRegistry;
import dan200.computercraft.shared.util.DataComponentUtil;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.neoforged.neoforge.event.village.WandererTradesEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VillagerTrade implements VillagerTrades.ItemListing {

    private final Type type;

    private final int emeraldAmount;
    private final int itemAmount;
    private final int maxUses;
    private final int xp;

    private final ItemLike item;
    private final ItemStack itemStack;

    private VillagerTrade(@NotNull Type type, int emeraldAmount, int itemAmount, int maxUses, int xp, ItemLike item, ItemStack itemStack) {
        this.type = type;
        this.emeraldAmount = emeraldAmount;
        this.itemAmount = itemAmount;
        this.maxUses = maxUses;
        this.xp = xp;
        this.item = item;
        this.itemStack = itemStack;
    }

    @Nullable
    @Override
    public MerchantOffer getOffer(@NotNull Entity trader, @NotNull RandomSource rand) {
        if (type == Type.EMERALD_FOR_ITEM) {
            if (itemStack != null)
                return new MerchantOffer(new ItemCost(itemStack.getItem()), new ItemStack(Items.EMERALD, emeraldAmount), maxUses, xp, 1);
            if (item != null)
                return new MerchantOffer(new ItemCost(new ItemStack(item, itemAmount).getItem()), new ItemStack(Items.EMERALD, emeraldAmount), maxUses, xp, 1);

        }
        if (type == Type.ITEM_FOR_EMERALD) {
            if (itemStack != null)
                return new MerchantOffer(new ItemCost(new ItemStack(Items.EMERALD, emeraldAmount).getItem()), itemStack, maxUses, xp, 1);
            if (item != null)
                return new MerchantOffer(new ItemCost(new ItemStack(Items.EMERALD, emeraldAmount).getItem()), new ItemStack(item, itemAmount), maxUses, xp, 1);
        }
        return null;
    }

    public enum Type {
        ITEM_FOR_EMERALD,
        EMERALD_FOR_ITEM
    }

    public static class TradeBuilder {

        private VillagerTradesEvent villagerEvent;
        private WandererTradesEvent wandererEvent;
        private final int professionLevel;

        private final Type type;

        private final int emeraldAmount;
        private int itemAmount = 1;
        private int maxUses = 10;
        private int xp = 2;

        private ItemLike item;
        private ItemStack itemStack;

        private TradeBuilder(VillagerTradesEvent event, Type type, int emeraldAmount, int professionLevel) {
            this.villagerEvent = event;
            this.type = type;
            this.emeraldAmount = emeraldAmount;
            this.professionLevel = professionLevel;
        }

        private TradeBuilder(WandererTradesEvent event, Type type, int emeraldAmount, int professionLevel) {
            this.wandererEvent = event;
            this.type = type;
            this.emeraldAmount = emeraldAmount;
            this.professionLevel = professionLevel;
        }

        /**
         * Creates a new TradeBuilder instance. Can be used to create villager trades for normal villagers
         * This one is for normal villagers with any type of{@link ItemLike}.
         * {@link net.minecraft.world.item.Item} or {@link net.minecraft.world.level.block.Block} as example.
         *
         * @param event this should be executed in an event - pass the villager trade event here
         * @param itemLike the item for trade
         * @param type the trade type
         * @param emeraldAmount the emerald amount of the trade
         * @param professionLevel the profession level of the villager. 1 to 5
         * @return a builder instance
         */
        public static TradeBuilder createTrade(VillagerTradesEvent event, ItemLike itemLike, Type type, int emeraldAmount, int professionLevel) {
            return new TradeBuilder(event, type, emeraldAmount, professionLevel).withItem(itemLike);
        }

        /**
         * Creates a new TradeBuilder instance. Can be used to create villager trades for normal villagers
         *
         * @param event this should be executed in an event - pass the villager trade event here
         * @param type the trade type
         * @param emeraldAmount the emerald amount of the trade
         * @param professionLevel the profession level of the villager. 1 to 5
         * @return a builder instance
         */
        public static TradeBuilder createTrade(VillagerTradesEvent event, Type type, int emeraldAmount, int professionLevel) {
            return new TradeBuilder(event, type, emeraldAmount, professionLevel);
        }

        /**
         * Creates a new TradeBuilder instance. Can be used to create villager trades for wandering traders.
         * This one is for wandering traders with any type of{@link ItemLike}.
         * {@link net.minecraft.world.item.Item} or {@link net.minecraft.world.level.block.Block} as example.
         *
         * @param event this should be executed in an event - pass the villager trade event here
         * @param itemLike the item for trade
         * @param type the trade type
         * @param emeraldAmount the emerald amount of the trade
         * @param professionLevel the profession level of the villager. 1 to 5
         * @return a builder instance
         */
        public static TradeBuilder createTrade(WandererTradesEvent event, ItemLike itemLike, Type type, int emeraldAmount, int professionLevel) {
            return new TradeBuilder(event, type, emeraldAmount, professionLevel).withItem(itemLike);
        }

        /**
         * Creates a new TradeBuilder instance. Can be used to create villager trades for wandering traders.
         *
         * @param event this should be executed in an event - pass the villager trade event here
         * @param type the trade type
         * @param emeraldAmount the emerald amount of the trade
         * @param professionLevel the profession level of the villager. 1 to 5
         * @return a builder instance
         */
        public static TradeBuilder createTrade(WandererTradesEvent event, Type type, int emeraldAmount, int professionLevel) {
            return new TradeBuilder(event, type, emeraldAmount, professionLevel);
        }

        public TradeBuilder withItem(ItemLike item) {
            this.item = item;
            return this;
        }

        public TradeBuilder withItemStack(ItemStack itemStack) {
            this.itemStack = itemStack;
            return this;
        }

        /**
         * Sets a pocket upgrade as the item stack
         *
         * @param advanced should it be an advanced pocket computer
         * @param id the id of the upgrade
         * @return the current instance of the builder
         */
        public TradeBuilder withPocketUpgrade(boolean advanced, ResourceLocation id) {
            RegistryAccess access = wandererEvent != null ? wandererEvent.getRegistryAccess() : villagerEvent.getRegistryAccess();

            Holder.Reference<IPocketUpgrade> pocketUpgrade = access.registryOrThrow(IPocketUpgrade.REGISTRY)
                    .getHolder(id)
                    .orElse(null);
            if (pocketUpgrade == null) {
                AdvancedPeripherals.debug("Pocket upgrade " + id + " does not exist or was removed by a datapack, skipping villager trade");
                return this;
            }

            ItemStack pocketStack = DataComponentUtil.createStack(advanced ? ModRegistry.Items.POCKET_COMPUTER_ADVANCED.get() : ModRegistry.Items.POCKET_COMPUTER_NORMAL.get(), ModRegistry.DataComponents.POCKET_UPGRADE.get(), UpgradeData.ofDefault(pocketUpgrade));

            return withItemStack(pocketStack);
        }

        /**
         * Sets a turtle upgrade as the item stack
         *
         * @param advanced should it be an advanced turtle
         * @param id the id of the upgrade
         * @return the current instance of the builder
         */
        public TradeBuilder withTurtleUpgrade(boolean advanced, ResourceLocation id) {
            RegistryAccess access = wandererEvent != null ? wandererEvent.getRegistryAccess() : villagerEvent.getRegistryAccess();

            Holder.Reference<ITurtleUpgrade> turtleUpgrade = access.registryOrThrow(ITurtleUpgrade.REGISTRY)
                    .getHolder(id)
                    .orElse(null);

            if (turtleUpgrade == null) {
                AdvancedPeripherals.debug("Pocket upgrade " + id + " does not exist or was removed by a datapack, skipping villager trade");
                return this;
            }

            ItemStack turtleStack = DataComponentUtil.createStack(advanced ? ModRegistry.Items.TURTLE_ADVANCED.get() : ModRegistry.Items.TURTLE_NORMAL.get(), ModRegistry.DataComponents.RIGHT_TURTLE_UPGRADE.get(), UpgradeData.ofDefault(turtleUpgrade));

            return withItemStack(turtleStack);
        }

        /**
         * Sets the item amount of the trade
         * Normally 1
         *
         * @param itemAmount the item amount
         * @return the current instance of the builder
         */
        public TradeBuilder setItemAmount(int itemAmount) {
            this.itemAmount = itemAmount;
            return this;
        }

        /**
         * Sets the max uses of the trade
         * Normally 2
         *
         * @param maxUses the item amount
         * @return the current instance of the builder
         */
        public TradeBuilder setMaxUses(int maxUses) {
            this.maxUses = maxUses;
            return this;
        }

        /**
         * Sets the xp of the trade
         * Normally 10
         *
         * @param xp the item amount
         * @return the current instance of the builder
         */
        public TradeBuilder setXp(int xp) {
            this.xp = xp;
            return this;
        }

        /**
         * Builds and places the villager trade into the given event.
         */
        public void build() {
            VillagerTrade trade = new VillagerTrade(type, emeraldAmount, itemAmount, maxUses, xp, item, itemStack);
            if(wandererEvent != null) {
                if(professionLevel == 1)
                    wandererEvent.getGenericTrades().add(trade);
                if(professionLevel == 2)
                    wandererEvent.getRareTrades().add(trade);
                return;
            }
            villagerEvent.getTrades().get(professionLevel).add(trade);
        }
    }

}
