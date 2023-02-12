package de.srendi.advancedperipherals;

import de.srendi.advancedperipherals.common.setup.APBlocks;
import de.srendi.advancedperipherals.common.setup.APRegistration;
import de.srendi.advancedperipherals.common.setup.CCRegistration;
import de.srendi.advancedperipherals.common.util.inventory.ItemUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;

import java.util.Collection;
import java.util.Set;

@Mod.EventBusSubscriber(modid = AdvancedPeripherals.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class APCreativeTab {

    @SubscribeEvent
    public static void registerCreativeTab(CreativeModeTabEvent.Register event) {
        event.registerCreativeModeTab(AdvancedPeripherals.getRL("creativetab"), APCreativeTab::populateCreativeTabBuilder);
    }

    private static void populateCreativeTabBuilder(CreativeModeTab.Builder builder) {
        builder.displayItems((set, out, unknownMagicBoolean) -> {
            APRegistration.ITEMS.getEntries().stream().map(RegistryObject::get).forEach(out::accept);
            out.acceptAll(pocketUpgrade(CCRegistration.ID.COLONY_POCKET));
            out.acceptAll(pocketUpgrade(CCRegistration.ID.CHATTY_POCKET));
            out.acceptAll(pocketUpgrade(CCRegistration.ID.PLAYER_POCKET));
            out.acceptAll(pocketUpgrade(CCRegistration.ID.ENVIRONMENT_POCKET));
            out.acceptAll(pocketUpgrade(CCRegistration.ID.GEOSCANNER_POCKET));

            out.acceptAll(turtleUpgrade(CCRegistration.ID.CHATTY_TURTLE));
            out.acceptAll(turtleUpgrade(CCRegistration.ID.CHUNKY_TURTLE));
            out.acceptAll(turtleUpgrade(CCRegistration.ID.COMPASS_TURTLE));
            out.acceptAll(turtleUpgrade(CCRegistration.ID.PLAYER_TURTLE));
            out.acceptAll(turtleUpgrade(CCRegistration.ID.ENVIRONMENT_TURTLE));
            out.acceptAll(turtleUpgrade(CCRegistration.ID.GEOSCANNER_TURTLE));

            out.acceptAll(turtleUpgrade(CCRegistration.ID.WEAK_AUTOMATA));
            out.acceptAll(turtleUpgrade(CCRegistration.ID.OP_WEAK_AUTOMATA));
            out.acceptAll(turtleUpgrade(CCRegistration.ID.HUSBANDRY_AUTOMATA));
            out.acceptAll(turtleUpgrade(CCRegistration.ID.OP_HUSBANDRY_AUTOMATA));
            out.acceptAll(turtleUpgrade(CCRegistration.ID.END_AUTOMATA));
            out.acceptAll(turtleUpgrade(CCRegistration.ID.OP_END_AUTOMATA));

        });
        builder.icon(() -> new ItemStack(APBlocks.CHAT_BOX.get()));
        builder.title(Component.translatable("advancedperipherals.name"));
    }

    private static Collection<ItemStack> pocketUpgrade(ResourceLocation pocketId) {
        return Set.of(ItemUtil.makePocket(ItemUtil.POCKET_NORMAL, pocketId.toString()),
                ItemUtil.makePocket(ItemUtil.POCKET_ADVANCED, pocketId.toString()));
    }

    private static Collection<ItemStack> turtleUpgrade(ResourceLocation pocketId) {
        return Set.of(ItemUtil.makeTurtle(ItemUtil.TURTLE_NORMAL, pocketId.toString()),
                ItemUtil.makeTurtle(ItemUtil.TURTLE_ADVANCED, pocketId.toString()));
    }

}
