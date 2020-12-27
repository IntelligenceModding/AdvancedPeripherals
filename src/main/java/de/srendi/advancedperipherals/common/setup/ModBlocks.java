package de.srendi.advancedperipherals.common.setup;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.blocks.ChatBox;
import de.srendi.advancedperipherals.common.blocks.EnvironmentDetector;
import de.srendi.advancedperipherals.common.blocks.SilverOre;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

import net.minecraftforge.fml.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {

    //Disabled until i need this
    //public static final RegistryObject<Block> SILVER_ORE = register("silver_ore", SilverOre::new);
    public static final RegistryObject<Block> ENVIRONMENT_DETECTOR = register("environment_detector", EnvironmentDetector::new);
    public static final RegistryObject<Block> CHAT_BOX = register("chat_box", ChatBox::new);

    static void register() {

    }

    private static <T extends Block>RegistryObject<T> registerNoItem(String name, Supplier<T> block) {
        return Registration.BLOCKS.register(name, block);
    }

    private static <T extends Block>RegistryObject<T> register(String name, Supplier<T> block) {
        RegistryObject<T> ret = registerNoItem(name, block);
        Registration.ITEMS.register(name, () -> new BlockItem(ret.get(), new Item.Properties().group(AdvancedPeripherals.TAB)));
        return ret;
    }
}
