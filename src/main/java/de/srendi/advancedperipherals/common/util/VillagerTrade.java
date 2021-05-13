package de.srendi.advancedperipherals.common.util;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MerchantOffer;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class VillagerTrade implements VillagerTrades.ITrade {

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
    public MerchantOffer getOffer(Entity trader, Random rand) {
        if (type == Type.EmeraldForItem) {
            if (itemStack != null)
                return new MerchantOffer(itemStack, new ItemStack(Items.EMERALD, emeraldPrice), maxUses, xp, 1);
            if (item != null)
                return new MerchantOffer(new ItemStack(item, itemAmount), new ItemStack(Items.EMERALD, emeraldPrice), maxUses, xp, 1);

            return new MerchantOffer(new ItemStack(block, itemAmount), new ItemStack(Items.EMERALD, emeraldPrice), maxUses, xp, 1);
        }
        if (type == Type.ItemForEmerald) {
            if (itemStack != null)
                return new MerchantOffer(new ItemStack(Items.EMERALD, emeraldPrice), itemStack, maxUses, xp, 1);
            if (item != null)
                return new MerchantOffer(new ItemStack(Items.EMERALD, emeraldPrice), new ItemStack(item, itemAmount), maxUses, xp, 1);

            return new MerchantOffer(new ItemStack(Items.EMERALD, emeraldPrice), new ItemStack(block, itemAmount), maxUses, xp, 1);
        }
        return null;
    }

    public enum Type {
        ItemForEmerald,
        EmeraldForItem
    }

}
