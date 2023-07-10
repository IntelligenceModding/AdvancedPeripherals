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

    private final Supplier<Boolean> enabledSup;

    public APBlockItem(Block blockIn, Properties properties, Supplier<Boolean> enabledSup) {
        super(blockIn, properties);
        this.enabledSup = enabledSup;
    }

    public APBlockItem(Block blockIn, Supplier<Boolean> enabledSup) {
        super(blockIn);
        this.enabledSup = enabledSup;
    }

    @Override
    public boolean isEnabled() {
        return enabledSup.get();
    }

    @Override
    public void fillItemCategory(@NotNull CreativeModeTab group, @NotNull NonNullList<ItemStack> items) {
        super.fillItemCategory(group, items);
        if(allowedIn(group))
            ItemUtil.addComputerItemToTab(turtleID, pocketID, items);
    }
}
