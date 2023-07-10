package de.srendi.advancedperipherals.common.items;

import de.srendi.advancedperipherals.common.items.base.BaseItem;
import de.srendi.advancedperipherals.common.util.inventory.ItemUtil;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class APItem extends BaseItem {

    private final Supplier<Boolean> enabledSup;

    public APItem(Properties properties, Supplier<Boolean> enabledSup) {
        super(properties);
        this.enabledSup = enabledSup;
    }

    public APItem(Supplier<Boolean> enabledSup) {
        super();
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
