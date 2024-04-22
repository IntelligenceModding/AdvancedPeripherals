package de.srendi.advancedperipherals;

import de.srendi.advancedperipherals.common.setup.Blocks;
import de.srendi.advancedperipherals.common.setup.CCRegistration;
import de.srendi.advancedperipherals.common.setup.Registration;
import de.srendi.advancedperipherals.common.util.inventory.ItemUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;
import java.util.Set;

public class APCreativeTab {

    public static void populateCreativeTabBuilder(CreativeModeTab.Builder builder) {
        builder.displayItems((set, out) -> {
            Registration.ITEMS.().stream().map(RegistryObject::get).forEach(out::accept);
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
        builder.icon(() -> new ItemStack(Blocks.CHAT_BOX.get()));
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
