package de.srendi.advancedperipherals.setup;

import de.srendi.advancedperipherals.items.SilverIngot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;


public class ModItems {
    public static final RegistryObject<Item> SILVER_INGOT = Registration.ITEMS.register("silver_ingot", SilverIngot::new);

    static void register() {

    }
}
