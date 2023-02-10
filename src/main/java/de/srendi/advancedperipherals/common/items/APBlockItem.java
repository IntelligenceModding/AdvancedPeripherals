package de.srendi.advancedperipherals.common.items;

import de.srendi.advancedperipherals.common.items.base.BaseBlockItem;
import de.srendi.advancedperipherals.common.util.inventory.ItemUtil;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class APBlockItem extends BaseBlockItem {

    @Nullable
    private final ResourceLocation turtleID;
    @Nullable
    private final ResourceLocation pocketID;
    private final Supplier<Boolean> enabledSup;

    public APBlockItem(Block blockIn, Properties properties, @Nullable ResourceLocation turtleID, @Nullable ResourceLocation pocketID, Supplier<Boolean> enabledSup) {
        super(blockIn, properties);
        this.turtleID = turtleID;
        this.pocketID = pocketID;
        this.enabledSup = enabledSup;
    }

    public APBlockItem(Block blockIn, @Nullable ResourceLocation turtleID, @Nullable ResourceLocation pocketID, Supplier<Boolean> enabledSup) {
        super(blockIn);
        this.turtleID = turtleID;
        this.pocketID = pocketID;
        this.enabledSup = enabledSup;
    }

    @Override
    public void fillItemCategory(@NotNull CreativeModeTab group, @NotNull NonNullList<ItemStack> items) {
        super.fillItemCategory(group, items);
        if(allowdedIn(group))
            ItemUtil.addCompuerItemToTab(turtleID, pocketID, items);
    }

    @Override
    public boolean isEnabled() {
        return enabledSup.get();
    }
}
