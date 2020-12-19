package de.srendi.advancedperipherals.common.items;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;

public class SilverIngot extends Item {

    public SilverIngot() {
        super(new Item.Properties().group(ItemGroup.MATERIALS).group(AdvancedPeripherals.TAB));
    }
}
