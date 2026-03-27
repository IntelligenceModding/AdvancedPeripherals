package de.srendi.advancedperipherals;

import dan200.computercraft.api.pocket.IPocketUpgrade;
import dan200.computercraft.api.turtle.ITurtleUpgrade;
import dan200.computercraft.api.upgrades.UpgradeBase;
import dan200.computercraft.api.upgrades.UpgradeData;
import dan200.computercraft.shared.ModRegistry;
import dan200.computercraft.shared.pocket.items.PocketComputerItem;
import dan200.computercraft.shared.turtle.items.TurtleItem;
import dan200.computercraft.shared.util.DataComponentUtil;
import de.srendi.advancedperipherals.common.setup.APBlocks;
import de.srendi.advancedperipherals.common.setup.APRegistration;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Objects;
import java.util.stream.Stream;

public class APCreativeTab {

    public static void populateCreativeTabBuilder(CreativeModeTab.Builder builder) {
        builder
            .icon(() -> new ItemStack(APBlocks.CHAT_BOX.get()))
            .title(Component.translatable("advancedperipherals.name"))
            .displayItems((set, out) -> {
                APRegistration.ITEMS.getEntries().stream().map(DeferredHolder::get).forEach(out::accept);

                addTurtle(out, ModRegistry.Items.TURTLE_NORMAL.get(), set.holders());
                addTurtle(out, ModRegistry.Items.TURTLE_ADVANCED.get(), set.holders());
                addPocket(out, ModRegistry.Items.POCKET_COMPUTER_NORMAL.get(), set.holders());
                addPocket(out, ModRegistry.Items.POCKET_COMPUTER_ADVANCED.get(), set.holders());
            });
    }

    // Friendly stolen from CC:Tweaked ModRegistry.class
    private static boolean isOurUpgrade(Holder.Reference<? extends UpgradeBase> upgrade) {
        String namespace = upgrade.key().location().getNamespace();
        return namespace.equals(AdvancedPeripherals.MOD_ID);
    }

    private static void addTurtle(CreativeModeTab.Output out, TurtleItem turtle, HolderLookup.Provider registries) {
        out.accept(new ItemStack(turtle));
        Stream<ItemStack> filteredItemStacks = registries.lookupOrThrow(ITurtleUpgrade.REGISTRY).listElements().filter(APCreativeTab::isOurUpgrade).map((x) -> DataComponentUtil.createStack(turtle, ModRegistry.DataComponents.RIGHT_TURTLE_UPGRADE.get(), UpgradeData.ofDefault(x)));
        Objects.requireNonNull(out);
        filteredItemStacks.forEach(out::accept);
    }

    private static void addPocket(CreativeModeTab.Output out, PocketComputerItem pocket, HolderLookup.Provider registries) {
        out.accept(new ItemStack(pocket));
        Stream<ItemStack> filteredItemStacks = registries.lookupOrThrow(IPocketUpgrade.REGISTRY).listElements().filter(APCreativeTab::isOurUpgrade).map((x) -> DataComponentUtil.createStack(pocket, ModRegistry.DataComponents.POCKET_UPGRADE.get(), UpgradeData.ofDefault(x)));
        Objects.requireNonNull(out);
        filteredItemStacks.forEach(out::accept);
    }

}
