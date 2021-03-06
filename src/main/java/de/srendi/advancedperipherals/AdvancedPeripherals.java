package de.srendi.advancedperipherals;

import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import de.srendi.advancedperipherals.common.configuration.ConfigHandler;
import de.srendi.advancedperipherals.common.configuration.ConfigHolder;
import de.srendi.advancedperipherals.common.setup.Blocks;
import de.srendi.advancedperipherals.common.setup.Registration;
import de.srendi.advancedperipherals.common.util.PlayerController;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(AdvancedPeripherals.MOD_ID)
public class AdvancedPeripherals {

    public static final String MOD_ID = "advancedperipherals";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static final PlayerController playerController = new PlayerController();
    public static final ItemGroup TAB = new ItemGroup("advancedperipheralstab") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(Blocks.CHAT_BOX.get());
        }
    };

    public AdvancedPeripherals() {
        LOGGER.info("AdvancedPeripherals says hello!");
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigHolder.COMMON_SPEC);
        MinecraftForge.EVENT_BUS.addListener(this::onWorldLoad);
        modBus.addListener(ConfigHandler::configEvent);
        Registration.register();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        playerController.init(event.getWorld());
    }

    public static void Debug(String message) {
        if(AdvancedPeripheralsConfig.enableDebugMode) {
            LOGGER.debug("[DEBUG] " + message);
        }
    }

    //TODO: Add more comments to the code
    //TODO: The code is a little bit messy...
}