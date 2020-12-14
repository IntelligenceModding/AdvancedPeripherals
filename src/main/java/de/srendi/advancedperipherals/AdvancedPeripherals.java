package de.srendi.advancedperipherals;

import de.srendi.advancedperipherals.addons.computercraft.ComputerCraft;
import de.srendi.advancedperipherals.setup.ModItems;
import de.srendi.advancedperipherals.setup.Registration;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(AdvancedPeripherals.MOD_ID)
public class AdvancedPeripherals {
    public static final String MOD_ID = "advancedperipherals";

    public static final Logger LOGGER = LogManager.getLogger();

    public AdvancedPeripherals() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        modBus.addListener(this::commonSetup);
        Registration.register();
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static final ItemGroup TAB = new ItemGroup("advancedperipheralstab") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModItems.SILVER_INGOT.get());
        }
    };

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.log(Level.INFO, "AdvancedPeripherals says hello!");

        ComputerCraft.initiate();
    }

    //TODO: Add more comments to the code

}
