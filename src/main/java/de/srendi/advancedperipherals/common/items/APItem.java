package de.srendi.advancedperipherals.common.items;

import de.srendi.advancedperipherals.common.items.base.BaseItem;
import de.srendi.advancedperipherals.common.util.ItemUtil;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class APItem extends BaseItem {

    @Nullable ResourceLocation turtleID;
    @Nullable ResourceLocation pocketID;
    ITextComponent description;
    Supplier<Boolean> enabledSup;

    public APItem(Properties properties, @Nullable ResourceLocation turtleID, @Nullable ResourceLocation pocketID, ITextComponent description, Supplier<Boolean> enabledSup) {
        super(properties);
        this.turtleID = turtleID;
        this.pocketID = pocketID;
        this.description = description;
        this.enabledSup = enabledSup;
    }

    public APItem(@Nullable ResourceLocation turtleID, @Nullable ResourceLocation pocketID, ITextComponent description, Supplier<Boolean> enabledSup) {
        super();
        this.turtleID = turtleID;
        this.pocketID = pocketID;
        this.description = description;
        this.enabledSup = enabledSup;
    }

    @Override
    public ITextComponent getDescription() {
        return description;
    }

    @Override
    public boolean isEnabled() {
        return enabledSup.get();
    }

    @Override
    public void fillItemCategory(@NotNull ItemGroup group, @NotNull NonNullList<ItemStack> items) {
        super.fillItemCategory(group, items);
        if (!allowdedIn(group))
            return;
        if (turtleID != null) {
            items.add(ItemUtil.makeTurtle(ItemUtil.TURTLE_ADVANCED, turtleID.toString()));
            items.add(ItemUtil.makeTurtle(ItemUtil.TURTLE_NORMAL, turtleID.toString()));
        }
        if (pocketID != null) {
            items.add(ItemUtil.makePocket(ItemUtil.POCKET_ADVANCED, pocketID.toString()));
            items.add(ItemUtil.makePocket(ItemUtil.POCKET_NORMAL, pocketID.toString()));
        }
    }
}
