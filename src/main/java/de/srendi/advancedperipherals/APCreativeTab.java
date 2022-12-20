package de.srendi.advancedperipherals;

import de.srendi.advancedperipherals.common.setup.Blocks;
import de.srendi.advancedperipherals.common.setup.Registration;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = AdvancedPeripherals.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class APCreativeTab {

    @SubscribeEvent
    public static void registerCreativeTab(CreativeModeTabEvent.Register event) {
        event.registerCreativeModeTab(AdvancedPeripherals.getRL("creativetab"), APCreativeTab::populateCreativeTabBuilder);
    }

    private static void populateCreativeTabBuilder(CreativeModeTab.Builder builder) {
        builder.displayItems((set, out, unknownMagicBoolean) -> {
            Registration.ITEMS.getEntries().stream().map(RegistryObject::get).forEach(out::accept);
        });
        builder.icon(() -> new ItemStack(Blocks.CHAT_BOX.get()));
        builder.title(Component.translatable("advancedperipherals.name"));
        builder.withSearchBar();
    }

}
