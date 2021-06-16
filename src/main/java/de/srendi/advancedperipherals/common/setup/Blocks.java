package de.srendi.advancedperipherals.common.setup;

import de.srendi.advancedperipherals.common.blocks.*;
import de.srendi.advancedperipherals.common.items.*;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.RegistryObject;

import java.util.function.Supplier;

public class Blocks {

    //TODO -1.17 fix the turtle and pocket ids of the chat box
    public static final RegistryObject<Block> ENVIRONMENT_DETECTOR = register("environment_detector", EnvironmentDetectorBlock::new,
            ()-> new APBlockItem(Blocks.ENVIRONMENT_DETECTOR.get(), "environment_detector_turtle", "environment_pocket",
                    new TranslationTextComponent("item.advancedperipherals.tooltip.environment_detector")));
    public static final RegistryObject<Block> CHAT_BOX = register("chat_box", ChatBoxBlock::new,
            ()-> new APBlockItem(Blocks.CHAT_BOX.get(), "chat_box_turtle", "chatty_turtle",
                    new TranslationTextComponent("item.advancedperipherals.tooltip.chat_box")));
    public static final RegistryObject<Block> PLAYER_DETECTOR = register("player_detector", PlayerDetectorBlock::new,
            ()-> new APBlockItem(Blocks.PLAYER_DETECTOR.get(), "player_detector_turtle", "player_pocket",
                    new TranslationTextComponent("item.advancedperipherals.tooltip.player_detector")));
    public static final RegistryObject<Block> ME_BRIDGE = register("me_bridge", MeBridgeBlock::new,
            ()-> new APBlockItem(Blocks.ME_BRIDGE.get(), null, null,
                    new TranslationTextComponent("item.advancedperipherals.tooltip.me_bridge")));
    public static final RegistryObject<Block> RS_BRIDGE = register("rs_bridge", RsBridgeBlock::new,
            ()-> new APBlockItem(Blocks.RS_BRIDGE.get(), null, null,
                    new TranslationTextComponent("item.advancedperipherals.tooltip.rs_bridge")));
    public static final RegistryObject<Block> ENERGY_DETECTOR = register("energy_detector", EnergyDetectorBlock::new,
            ()-> new APBlockItem(Blocks.ENERGY_DETECTOR.get(), null, null,
                    new TranslationTextComponent("item.advancedperipherals.tooltip.energy_detector")));
    public static final RegistryObject<Block> PERIPHERAL_CASING = register("peripheral_casing", PeripheralCasingBlock::new,
            ()-> new APBlockItem(Blocks.PERIPHERAL_CASING.get(), new Item.Properties().stacksTo(16), null, null,
                    new TranslationTextComponent("item.advancedperipherals.tooltip.peripheral_casing")));
    public static final RegistryObject<Block> AR_CONTROLLER = register("ar_controller", ARControllerBlock::new,
            ()-> new APBlockItem(Blocks.AR_CONTROLLER.get(), null, null,
                    new TranslationTextComponent("item.advancedperipherals.tooltip.ar_controller")));
    public static final RegistryObject<Block> INVENTORY_MANAGER = register("inventory_manager", InventoryManagerBlock::new,
            ()-> new APBlockItem(Blocks.INVENTORY_MANAGER.get(), null, null,
                new TranslationTextComponent("item.advancedperipherals.tooltip.inventory_manager")));
    public static final RegistryObject<Block> REDSTONE_INTEGRATOR = register("redstone_integrator", RedstoneIntegratorBlock::new,
            ()-> new APBlockItem(Blocks.REDSTONE_INTEGRATOR.get(), null, null,
                new TranslationTextComponent("item.advancedperipherals.tooltip.redstone_integrator")));
    public static final RegistryObject<Block> PERIPHERAL_PROXY = register("peripheral_proxy", PeripheralProxyBlock::new,
            ()-> new APBlockItem(Blocks.PERIPHERAL_PROXY.get(), null, null,
                new TranslationTextComponent("item.advancedperipherals.tooltip.peripheral_proxy")));

    static void register() {
    }

    private static <T extends Block> RegistryObject<T> registerNoItem(String name, Supplier<T> block) {
        return Registration.BLOCKS.register(name, block);
    }

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> block, Supplier<BlockItem> blockItem) {
        RegistryObject<T> registryObject = registerNoItem(name, block);
        Registration.ITEMS.register(name, blockItem);
        return registryObject;
    }
}
