package de.srendi.advancedperipherals.common.village;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.event.village.WandererTradesEvent;
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
                return new MerchantOffer(itemStack, new ItemStack(Items.EMERALD, emeraldAmount), maxUses, xp, 1);
            if (item != null)
                return new MerchantOffer(new ItemStack(item, itemAmount), new ItemStack(Items.EMERALD, emeraldAmount), maxUses, xp, 1);

        }
        if (type == Type.ITEM_FOR_EMERALD) {
            if (itemStack != null)
                return new MerchantOffer(new ItemStack(Items.EMERALD, emeraldAmount), itemStack, maxUses, xp, 1);
            if (item != null)
                return new MerchantOffer(new ItemStack(Items.EMERALD, emeraldAmount), new ItemStack(item, itemAmount), maxUses, xp, 1);
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

        private final ItemLike item;
        private final ItemStack itemStack;

        private TradeBuilder(VillagerTradesEvent event, ItemLike item, Type type, int emeraldAmount, int professionLevel) {
            this.villagerEvent = event;
            this.item = item;
            this.itemStack = null;
            this.type = type;
            this.emeraldAmount = emeraldAmount;
            this.professionLevel = professionLevel;
        }

        private TradeBuilder(VillagerTradesEvent event, ItemStack stack, Type type, int emeraldAmount, int professionLevel) {
            this.villagerEvent = event;
            this.itemStack = stack;
            this.item = null;
            this.type = type;
            this.emeraldAmount = emeraldAmount;
            this.professionLevel = professionLevel;
        }

        private TradeBuilder(WandererTradesEvent event, ItemLike item, Type type, int emeraldAmount, int professionLevel) {
            this.wandererEvent = event;
            this.item = item;
            this.itemStack = null;
            this.type = type;
            this.emeraldAmount = emeraldAmount;
            this.professionLevel = professionLevel;
        }

        private TradeBuilder(WandererTradesEvent event, ItemStack stack, Type type, int emeraldAmount, int professionLevel) {
            this.wandererEvent = event;
            this.itemStack = stack;
            this.item = null;
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
            return new TradeBuilder(event, itemLike, type, emeraldAmount, professionLevel);
        }

        /**
         * Creates a new TradeBuilder instance. Can be used to create villager trades for normal villagers
         * This one is for normal villagers with item stacks
         *
         * @param event this should be executed in an event - pass the villager trade event here
         * @param itemStack the item for trade
         * @param type the trade type
         * @param emeraldAmount the emerald amount of the trade
         * @param professionLevel the profession level of the villager. 1 to 5
         * @return a builder instance
         */
        public static TradeBuilder createTrade(VillagerTradesEvent event, ItemStack itemStack, Type type, int emeraldAmount, int professionLevel) {
            return new TradeBuilder(event, itemStack, type, emeraldAmount, professionLevel);
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
            return new TradeBuilder(event, itemLike, type, emeraldAmount, professionLevel);
        }

        /**
         * Creates a new TradeBuilder instance. Can be used to create villager trades for wandering traders.
         * This one is for normal villagers with item stacks
         *
         * @param event this should be executed in an event - pass the villager trade event here
         * @param itemStack the item for trade
         * @param type the trade type
         * @param emeraldAmount the emerald amount of the trade
         * @param professionLevel the profession level of the villager. 1 to 5
         * @return a builder instance
         */
        public static TradeBuilder createTrade(WandererTradesEvent event, ItemStack itemStack, Type type, int emeraldAmount, int professionLevel) {
            return new TradeBuilder(event, itemStack, type, emeraldAmount, professionLevel);
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
