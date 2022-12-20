package de.srendi.advancedperipherals.common.village;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VillagerTrade implements VillagerTrades.ItemListing {

    private final Type type;

    private int emeraldPrice;
    private int itemAmount = 1;

    private Item item;
    private Block block;
    private ItemStack itemStack;

    private int maxUses = 1;
    private int xp = 5;

    public VillagerTrade(Type type) {
        this.type = type;
    }

    public VillagerTrade setItemAmount(int itemAmount) {
        this.itemAmount = itemAmount;
        return this;
    }

    public VillagerTrade setEmeraldPrice(int emeraldPrice) {
        this.emeraldPrice = emeraldPrice;
        return this;
    }

    public VillagerTrade setItem(Block block) {
        this.block = block;
        return this;
    }

    public VillagerTrade setItem(Item item) {
        this.item = item;
        return this;
    }

    public VillagerTrade setMaxUses(int maxUses) {
        this.maxUses = maxUses;
        return this;
    }

    public VillagerTrade setXp(int xp) {
        this.xp = xp;
        return this;
    }

    public VillagerTrade setItem(ItemStack itemStack) {
        this.itemStack = itemStack;
        return this;
    }

    @Nullable
    @Override
    public MerchantOffer getOffer(@NotNull Entity trader, @NotNull RandomSource rand) {
        if (type == Type.EMERALD_FOR_ITEM) {
            if (itemStack != null)
                return new MerchantOffer(itemStack, new ItemStack(Items.EMERALD, emeraldPrice), maxUses, xp, 1);
            if (item != null)
                return new MerchantOffer(new ItemStack(item, itemAmount), new ItemStack(Items.EMERALD, emeraldPrice), maxUses, xp, 1);

            return new MerchantOffer(new ItemStack(block, itemAmount), new ItemStack(Items.EMERALD, emeraldPrice), maxUses, xp, 1);
        }
        if (type == Type.ITEM_FOR_EMERALD) {
            if (itemStack != null)
                return new MerchantOffer(new ItemStack(Items.EMERALD, emeraldPrice), itemStack, maxUses, xp, 1);
            if (item != null)
                return new MerchantOffer(new ItemStack(Items.EMERALD, emeraldPrice), new ItemStack(item, itemAmount), maxUses, xp, 1);

            return new MerchantOffer(new ItemStack(Items.EMERALD, emeraldPrice), new ItemStack(block, itemAmount), maxUses, xp, 1);
        }
        return null;
    }

    public enum Type {
        ITEM_FOR_EMERALD,
        EMERALD_FOR_ITEM
    }

}
