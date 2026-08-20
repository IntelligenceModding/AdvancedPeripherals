package de.srendi.advancedperipherals;

import dan200.computercraft.api.upgrades.UpgradeBase;
import dan200.computercraft.impl.PocketUpgrades;
import dan200.computercraft.impl.TurtleUpgrades;
import dan200.computercraft.shared.ModRegistry;
import dan200.computercraft.shared.pocket.items.PocketComputerItem;
import dan200.computercraft.shared.turtle.items.TurtleItem;
import de.srendi.advancedperipherals.common.setup.APBlocks;
import de.srendi.advancedperipherals.common.setup.APRegistration;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.RegistryObject;

import java.util.stream.Stream;

public class APCreativeTab {

    public static void populateCreativeTabBuilder(CreativeModeTab.Builder builder) {
        builder
            .icon(() -> new ItemStack(APBlocks.CHAT_BOX.get()))
            .title(Component.translatable("advancedperipherals.name"))
            .displayItems((set, out) -> {
                APRegistration.ITEMS.getEntries().stream().map(RegistryObject::get).forEach(out::accept);

                addTurtle(out, ModRegistry.Items.TURTLE_NORMAL.get(), set.holders());
                addTurtle(out, ModRegistry.Items.TURTLE_ADVANCED.get(), set.holders());
                addPocket(out, ModRegistry.Items.POCKET_COMPUTER_NORMAL.get(), set.holders());
                addPocket(out, ModRegistry.Items.POCKET_COMPUTER_ADVANCED.get(), set.holders());
            });
    }

    // Friendly stolen from CC:Tweaked ModRegistry.class
    private static boolean isOurUpgrade(UpgradeBase upgrade) {
        String namespace = upgrade.getUpgradeID().getNamespace();
        return namespace.equals(AdvancedPeripherals.MOD_ID);
    }

    private static void addTurtle(CreativeModeTab.Output out, TurtleItem turtle, HolderLookup.Provider registries) {
        out.accept(new ItemStack(turtle));
        Stream<ItemStack> filteredItemStacks = TurtleUpgrades.instance().getUpgrades().stream()
            .filter(APCreativeTab::isOurUpgrade)
            .map((value) -> {
                ItemStack stack = new ItemStack(turtle);
                stack.getOrCreateTag().putString("RightUpgrade", value.getUpgradeID().toString());
                return stack;
            });
        filteredItemStacks.forEach(out::accept);
    }

    private static void addPocket(CreativeModeTab.Output out, PocketComputerItem pocket, HolderLookup.Provider registries) {
        out.accept(new ItemStack(pocket));
        Stream<ItemStack> filteredItemStacks = PocketUpgrades.instance().getUpgrades().stream()
            .filter(APCreativeTab::isOurUpgrade)
            .map((value) -> {
                ItemStack stack = new ItemStack(pocket);
                stack.getOrCreateTag().putString("Upgrade", value.getUpgradeID().toString());
                return stack;
            });
        filteredItemStacks.forEach(out::accept);
    }

}
