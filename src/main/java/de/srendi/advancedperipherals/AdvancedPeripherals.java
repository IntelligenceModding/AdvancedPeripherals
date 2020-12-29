package de.srendi.advancedperipherals;

import de.srendi.advancedperipherals.common.addons.computercraft.ComputerCraft;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import de.srendi.advancedperipherals.common.setup.ModBlocks;
import de.srendi.advancedperipherals.common.setup.ModItems;
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
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(AdvancedPeripherals.MOD_ID)
public class AdvancedPeripherals {

    public static final String MOD_ID = "advancedperipherals";

    public static final Logger LOGGER = LogManager.getLogger();

    public static final PlayerController playerController = new PlayerController();

    public AdvancedPeripherals() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, AdvancedPeripheralsConfig.COMMON_CONFIG);
        AdvancedPeripheralsConfig.loadConfig(AdvancedPeripheralsConfig.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve("advancedperipherals-common.toml"));

        MinecraftForge.EVENT_BUS.addListener(this::onWorldLoad);
        modBus.addListener(this::commonSetup);
        Registration.register();
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static final ItemGroup TAB = new ItemGroup("advancedperipheralstab") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModBlocks.ENVIRONMENT_DETECTOR.get());
        }
    };

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.log(Level.INFO, "AdvancedPeripherals says hello!");

        ComputerCraft.initiate();
    }

    public Logger getLogger() {
        return LOGGER;
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        playerController.init(event.getWorld());
    }

    //TODO: Add more comments to the code
}