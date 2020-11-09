package de.srendi.advancedperipherals.setup;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;


public class ModItems {
    public static final RegistryObject<Item> SILVER_INGOT = Registration.ITEMS.register("siver_ingot", () ->
            new Item(new Item.Properties().group(ItemGroup.MATERIALS).maxDamage(200000)));

    static void register() {

    }
}
