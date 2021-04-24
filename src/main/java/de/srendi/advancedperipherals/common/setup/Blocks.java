package de.srendi.advancedperipherals.common.setup;

import de.srendi.advancedperipherals.common.blocks.*;
import de.srendi.advancedperipherals.common.items.*;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraftforge.fml.RegistryObject;

import java.util.function.Supplier;

public class Blocks {

    public static final RegistryObject<Block> ENVIRONMENT_DETECTOR = register("environment_detector", EnvironmentDetectorBlock::new, EnvironmentDetectorItem::new);
    public static final RegistryObject<Block> CHAT_BOX = register("chat_box", ChatBoxBlock::new, ChatBoxItem::new);
    public static final RegistryObject<Block> PLAYER_DETECTOR = register("player_detector", PlayerDetectorBlock::new, PlayerDetectorItem::new);
    public static final RegistryObject<Block> ME_BRIDGE = register("me_bridge", MeBridgeBlock::new, MeBridgeItem::new);
    public static final RegistryObject<Block> RS_BRIDGE = register("rs_bridge", RsBridgeBlock::new, RsBridgeItem::new);
    public static final RegistryObject<Block> ENERGY_DETECTOR = register("energy_detector", EnergyDetectorBlock::new, EnergyDetectorItem::new);
    public static final RegistryObject<Block> PERIPHERAL_CASING = register("peripheral_casing", PeripheralCasingBlock::new, PeripheralCasingItem::new);
    public static final RegistryObject<Block> INVENTORY_MANAGER = register("inventory_manager", InventoryManagerBlock::new, InventoryManagerItem::new);

    static void register() {}

    private static <T extends Block> RegistryObject<T> registerNoItem(String name, Supplier<T> block) {
        return Registration.BLOCKS.register(name, block);
    }

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> block, Supplier<BlockItem> blockItem) {
        RegistryObject<T> registryObject = registerNoItem(name, block);
        Registration.ITEMS.register(name, blockItem);
        return registryObject;
    }
}
