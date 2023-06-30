package de.srendi.advancedperipherals;

import de.srendi.advancedperipherals.common.setup.APBlocks;
import de.srendi.advancedperipherals.common.setup.APRegistration;
import de.srendi.advancedperipherals.common.setup.CCRegistration;
import de.srendi.advancedperipherals.common.util.inventory.ItemUtil;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;

public class APCreativeTab extends CreativeModeTab {
    public APCreativeTab() {
        super("advancedperipheralstab");
    }

    @Override
    @NotNull
    public ItemStack makeIcon() {
        return new ItemStack(APBlocks.CHAT_BOX.get());
    }

    @Override
    public void fillItemList(@NotNull NonNullList<ItemStack> pItems) {
        APRegistration.ITEMS.getEntries().stream().map(RegistryObject::get).forEach(item -> pItems.add(new ItemStack(item)));
        pItems.addAll(pocketUpgrade(CCRegistration.ID.COLONY_POCKET));
        pItems.addAll(pocketUpgrade(CCRegistration.ID.CHATTY_POCKET));
        pItems.addAll(pocketUpgrade(CCRegistration.ID.PLAYER_POCKET));
        pItems.addAll(pocketUpgrade(CCRegistration.ID.ENVIRONMENT_POCKET));
        pItems.addAll(pocketUpgrade(CCRegistration.ID.GEOSCANNER_POCKET));

        pItems.addAll(turtleUpgrade(CCRegistration.ID.CHATTY_TURTLE));
        pItems.addAll(turtleUpgrade(CCRegistration.ID.CHUNKY_TURTLE));
        pItems.addAll(turtleUpgrade(CCRegistration.ID.COMPASS_TURTLE));
        pItems.addAll(turtleUpgrade(CCRegistration.ID.PLAYER_TURTLE));
        pItems.addAll(turtleUpgrade(CCRegistration.ID.ENVIRONMENT_TURTLE));
        pItems.addAll(turtleUpgrade(CCRegistration.ID.GEOSCANNER_TURTLE));

        pItems.addAll(turtleUpgrade(CCRegistration.ID.WEAK_AUTOMATA));
        pItems.addAll(turtleUpgrade(CCRegistration.ID.OP_WEAK_AUTOMATA));
        pItems.addAll(turtleUpgrade(CCRegistration.ID.HUSBANDRY_AUTOMATA));
        pItems.addAll(turtleUpgrade(CCRegistration.ID.OP_HUSBANDRY_AUTOMATA));
        pItems.addAll(turtleUpgrade(CCRegistration.ID.END_AUTOMATA));
        pItems.addAll(turtleUpgrade(CCRegistration.ID.OP_END_AUTOMATA));
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
