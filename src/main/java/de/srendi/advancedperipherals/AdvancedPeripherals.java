package de.srendi.advancedperipherals;

import dan200.computercraft.api.ComputerCraftAPI;
import de.srendi.advancedperipherals.blocks.LightSensor;
import de.srendi.advancedperipherals.setup.ModItems;
import de.srendi.advancedperipherals.setup.Registration;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(AdvancedPeripherals.MOD_ID)
public class AdvancedPeripherals {
    public static final String MOD_ID = "advancedperipherals";

    public static final Logger LOGGER = LogManager.getLogger();

    public AdvancedPeripherals() {
        Registration.register();

        MinecraftForge.EVENT_BUS.register(this);
        ComputerCraftAPI.registerPeripheralProvider(new LightSensor());
    }

    public static final ItemGroup TAB = new ItemGroup("advancedperipheralstab") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModItems.SILVER_INGOT.get());
        }
    };

}
