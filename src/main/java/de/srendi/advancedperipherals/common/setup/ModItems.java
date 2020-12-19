package de.srendi.advancedperipherals.common.setup;

import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import de.srendi.advancedperipherals.common.items.*;

public class ModItems {
    public static final RegistryObject<Item> SILVER_INGOT = Registration.ITEMS.register("silver_ingot", SilverIngot::new);

    static void register() {

    }
}
