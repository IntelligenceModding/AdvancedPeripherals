package de.srendi.advancedperipherals;

import dan200.computercraft.api.ComputerCraftAPI;
import de.srendi.advancedperipherals.blocks.ChatBox;
import de.srendi.advancedperipherals.setup.ModItems;
import de.srendi.advancedperipherals.setup.Registration;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(AdvancedPeripherals.MOD_ID)
public class AdvancedPeripherals {
    public static final String MOD_ID = "advancedperipherals";

    private static final Logger LOGGER = LogManager.getLogger();

    public AdvancedPeripherals() {
        Registration.register();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        ComputerCraftAPI.registerPeripheralProvider(new ChatBox());
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        ComputerCraftAPI.registerPeripheralProvider(new ChatBox());

    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
        InterModComms.sendTo("advancedperipherals", "helloworld", ()->{
            LOGGER.info("Hello world from the MDK");
            return "Hello world";
        });
    }

    private void processIMC(final InterModProcessEvent event) {
        ComputerCraftAPI.registerPeripheralProvider(new ChatBox());
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        
    }

    public static final ItemGroup TAB = new ItemGroup("advancedperipheralstab") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModItems.SILVER_INGOT.get());
        }
    };

}
