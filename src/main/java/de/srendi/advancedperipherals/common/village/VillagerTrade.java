package de.srendi.advancedperipherals.common.village;

import dan200.computercraft.api.pocket.IPocketUpgrade;
import dan200.computercraft.api.turtle.ITurtleUpgrade;
import dan200.computercraft.impl.PocketUpgrades;
import dan200.computercraft.impl.TurtleUpgrades;
import dan200.computercraft.shared.ModRegistry;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import net.minecraft.resources.ResourceLocation;
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
    private final int maxUses;
    private final int xp;

    private final ItemStack itemStack;

    private VillagerTrade(@NotNull Type type, int emeraldAmount, @NotNull ItemStack itemStack, int maxUses, int xp) {
        this.type = type;
        this.emeraldAmount = emeraldAmount;
        this.maxUses = maxUses;
        this.xp = xp;
        this.itemStack = itemStack;
    }

    @Override
    @Nullable
    public MerchantOffer getOffer(@NotNull Entity trader, @NotNull RandomSource rand) {
        return switch (type) {
            case EMERALD_FOR_ITEM ->
                new MerchantOffer(itemStack, new ItemStack(Items.EMERALD, emeraldAmount), maxUses, xp, 1);
            case ITEM_FOR_EMERALD ->
                new MerchantOffer(new ItemStack(Items.EMERALD, emeraldAmount), itemStack, maxUses, xp, 1);
        };
    }

    public enum Type {
        ITEM_FOR_EMERALD,
        EMERALD_FOR_ITEM
    }

    public static class TradeBuilder {
        private VillagerTradesEvent villagerEvent;
        private WandererTradesEvent wandererEvent;
        private final Type type;

        private int professionLevel = 1;

        private int emeraldAmount = 1;
        private int maxUses = 10;
        private int xp = 2;

        private ItemStack itemStack = ItemStack.EMPTY;

        public TradeBuilder(VillagerTradesEvent event, Type type) {
            this.villagerEvent = event;
            this.type = type;
        }

        public TradeBuilder(WandererTradesEvent event, Type type) {
            this.wandererEvent = event;
            this.type = type;
        }

        public TradeBuilder withProfessionLevel(int level) {
            this.professionLevel = level;
            return this;
        }

        public TradeBuilder withEmeralds(int count) {
            this.emeraldAmount = count;
            return this;
        }

        public TradeBuilder withItem(ItemLike item) {
            this.itemStack = new ItemStack(item, this.itemStack.isEmpty() ? 1 : this.itemStack.getCount());
            return this;
        }

        /**
         * Sets the item amount of the trade
         * Normally 1
         *
         * @param count the item amount
         * @return the current instance of the builder
         */
        public TradeBuilder withItemAmount(int count) {
            this.itemStack = this.itemStack.copyWithCount(count);
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
            IPocketUpgrade pocketUpgrade = PocketUpgrades.instance().get(id.toString());

            if (pocketUpgrade == null) {
                AdvancedPeripherals.debug("Pocket upgrade {} does not exist or was removed by a datapack, skipping villager trade", id);
                return this;
            }

            ItemStack pocketStack = new ItemStack(advanced ? ModRegistry.Items.POCKET_COMPUTER_ADVANCED.get() : ModRegistry.Items.POCKET_COMPUTER_NORMAL.get());
            pocketStack.getOrCreateTag().putString("Upgrade", id.toString());
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
            ITurtleUpgrade turtleUpgrade = TurtleUpgrades.instance().get(id.toString());

            if (turtleUpgrade == null) {
                AdvancedPeripherals.debug("Turtle upgrade {} does not exist or was removed by a datapack, skipping villager trade", id);
                return this;
            }

            ItemStack turtleStack = new ItemStack(advanced ? ModRegistry.Items.TURTLE_ADVANCED.get() : ModRegistry.Items.TURTLE_NORMAL.get());
            turtleStack.getOrCreateTag().putString("RightUpgrade", id.toString());
            return withItemStack(turtleStack);
        }

        /**
         * Sets the max uses of the trade
         * Normally 2
         *
         * @param maxUses the item amount
         * @return the current instance of the builder
         */
        public TradeBuilder withMaxUses(int maxUses) {
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
        public TradeBuilder withXp(int xp) {
            this.xp = xp;
            return this;
        }

        /**
         * Builds and places the villager trade into the given event.
         */
        public void build() {
            VillagerTrade trade = new VillagerTrade(type, emeraldAmount, itemStack, maxUses, xp);
            if (wandererEvent != null) {
                if (professionLevel == 1) {
                    wandererEvent.getGenericTrades().add(trade);
                }
                if (professionLevel == 2) {
                    wandererEvent.getRareTrades().add(trade);
                }
                return;
            }
            villagerEvent.getTrades().get(professionLevel).add(trade);
        }
    }
}
