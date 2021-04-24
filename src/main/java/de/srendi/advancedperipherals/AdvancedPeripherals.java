package de.srendi.advancedperipherals;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.srendi.advancedperipherals.client.HudOverlayHandler;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import de.srendi.advancedperipherals.common.configuration.ConfigHandler;
import de.srendi.advancedperipherals.common.configuration.ConfigHolder;
import de.srendi.advancedperipherals.common.setup.Blocks;
import de.srendi.advancedperipherals.common.setup.Registration;
import de.srendi.advancedperipherals.common.util.ChunkManager;
import de.srendi.advancedperipherals.network.MNetwork;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import top.theillusivec4.curios.api.SlotTypeMessage;

@Mod(AdvancedPeripherals.MOD_ID)
public class AdvancedPeripherals {

    public static final String MOD_ID = "advancedperipherals";
    public static final Logger LOGGER = LogManager.getLogger("Advanced Peripherals");
    public static final ItemGroup TAB = new ItemGroup("advancedperipheralstab") {

        @Override
        public ItemStack createIcon() {
            return new ItemStack(Blocks.CHAT_BOX.get());
        }
    };

    private static boolean curiosLoaded;

    public AdvancedPeripherals() {
        LOGGER.info("AdvancedPeripherals says hello!");
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigHolder.COMMON_SPEC);
        modBus.addListener(this::commonSetup);
        modBus.addListener(ConfigHandler::configEvent);
        modBus.addListener(this::interModComms);
        modBus.addListener(this::clientSetup);
        Registration.register();
        MinecraftForge.EVENT_BUS.register(this);
        curiosLoaded = ModList.get().isLoaded("curios");
    }

    @SubscribeEvent
    public void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(ChunkManager::register);
        MNetwork.init();
    }

    public static void Debug(String message) {
        if (AdvancedPeripheralsConfig.enableDebugMode)
            LOGGER.debug("[DEBUG] " + message);
    }

    public static void Debug(String message, Level level) {
        if (AdvancedPeripheralsConfig.enableDebugMode)
            LOGGER.log(level, "[DEBUG] " + message);
    }

    public void clientSetup(FMLClientSetupEvent event) {
    	HudOverlayHandler.init();
    }

    @SubscribeEvent
    public void interModComms(InterModEnqueueEvent event) {
    	if (!curiosLoaded)
    		return;
    	InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, ()->new SlotTypeMessage.Builder("glasses").size(1).build());
    }

	public static boolean isCuriosLoaded() {
		return curiosLoaded;
	}

}