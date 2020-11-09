package de.srendi.advancedperipherals.setup;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;


public class ModItems {
    public static final RegistryObject<Item> SILVER_INGOT = registerItem("silver_ingot", ItemGroup.MATERIALS);

    static void register() {

    }

    private static RegistryObject<Item> registerItem(String name, ItemGroup group) {
        Registration.ITEMS.register(name, () -> new Item(new Item.Properties().group(group)));
        return null;
    }
}
