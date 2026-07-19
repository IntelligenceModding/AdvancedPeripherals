package de.srendi.advancedperipherals.common.data;

import dan200.computercraft.api.turtle.ITurtleUpgrade;
import de.srendi.advancedperipherals.common.addons.computercraft.turtles.TurtleBlockReaderUpgrade;
import de.srendi.advancedperipherals.common.addons.computercraft.turtles.TurtleChatBoxUpgrade;
import de.srendi.advancedperipherals.common.addons.computercraft.turtles.TurtleChunkyUpgrade;
import de.srendi.advancedperipherals.common.addons.computercraft.turtles.TurtleCompassUpgrade;
import de.srendi.advancedperipherals.common.addons.computercraft.turtles.TurtleEnvironmentDetectorUpgrade;
import de.srendi.advancedperipherals.common.addons.computercraft.turtles.TurtleGeoScannerUpgrade;
import de.srendi.advancedperipherals.common.addons.computercraft.turtles.TurtlePlayerDetectorUpgrade;
import de.srendi.advancedperipherals.common.addons.computercraft.turtles.TurtleSaddleUpgrade;
import de.srendi.advancedperipherals.common.addons.computercraft.turtles.metaphysics.EndAutomata;
import de.srendi.advancedperipherals.common.addons.computercraft.turtles.metaphysics.HusbandryAutomata;
import de.srendi.advancedperipherals.common.addons.computercraft.turtles.metaphysics.OverpoweredEndAutomata;
import de.srendi.advancedperipherals.common.addons.computercraft.turtles.metaphysics.OverpoweredHusbandryAutomata;
import de.srendi.advancedperipherals.common.addons.computercraft.turtles.metaphysics.OverpoweredWeakAutomata;
import de.srendi.advancedperipherals.common.addons.computercraft.turtles.metaphysics.WeakAutomata;
import de.srendi.advancedperipherals.common.setup.APBlocks;
import de.srendi.advancedperipherals.common.setup.APItems;
import de.srendi.advancedperipherals.common.setup.CCRegistration;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class TurtleUpgradesProvider {

    public static void addUpgrades(BootstrapContext<ITurtleUpgrade> upgrades) {
        upgrades.register(id(CCRegistration.ID.Turtle.BLOCK_READER), new TurtleBlockReaderUpgrade(new ItemStack(APBlocks.BLOCK_READER.get())));
        upgrades.register(id(CCRegistration.ID.Turtle.CHATTY), new TurtleChatBoxUpgrade(new ItemStack(APBlocks.CHAT_BOX.get())));
        upgrades.register(id(CCRegistration.ID.Turtle.CHUNKY), new TurtleChunkyUpgrade(new ItemStack(APItems.CHUNK_CONTROLLER)));
        upgrades.register(id(CCRegistration.ID.Turtle.COMPASS), new TurtleCompassUpgrade(new ItemStack(Items.COMPASS)));
        upgrades.register(id(CCRegistration.ID.Turtle.END_AUTOMATA), new EndAutomata(new ItemStack(APItems.END_AUTOMATA_CORE.get())));
        upgrades.register(id(CCRegistration.ID.Turtle.ENVIRONMENT), new TurtleEnvironmentDetectorUpgrade(new ItemStack(APBlocks.ENVIRONMENT_DETECTOR.get())));
        upgrades.register(id(CCRegistration.ID.Turtle.GEOSCANNER), new TurtleGeoScannerUpgrade(new ItemStack(APBlocks.GEO_SCANNER.get())));
        upgrades.register(id(CCRegistration.ID.Turtle.HUSBANDRY_AUTOMATA), new HusbandryAutomata(new ItemStack(APItems.HUSBANDRY_AUTOMATA_CORE.get())));
        upgrades.register(id(CCRegistration.ID.Turtle.OP_END_AUTOMATA), new OverpoweredEndAutomata(new ItemStack(APItems.OVERPOWERED_END_AUTOMATA_CORE.get())));
        upgrades.register(id(CCRegistration.ID.Turtle.OP_HUSBANDRY_AUTOMATA), new OverpoweredHusbandryAutomata(new ItemStack(APItems.OVERPOWERED_HUSBANDRY_AUTOMATA_CORE.get())));
        upgrades.register(id(CCRegistration.ID.Turtle.OP_WEAK_AUTOMATA), new OverpoweredWeakAutomata(new ItemStack(APItems.OVERPOWERED_WEAK_AUTOMATA_CORE.get())));
        upgrades.register(id(CCRegistration.ID.Turtle.PLAYER), new TurtlePlayerDetectorUpgrade(new ItemStack(APBlocks.PLAYER_DETECTOR.get())));
        upgrades.register(id(CCRegistration.ID.Turtle.SADDLE), new TurtleSaddleUpgrade(new ItemStack(Items.SADDLE)));
        upgrades.register(id(CCRegistration.ID.Turtle.WEAK_AUTOMATA), new WeakAutomata(new ItemStack(APItems.WEAK_AUTOMATA_CORE.get())));
    }

    public static ResourceKey<ITurtleUpgrade> id(ResourceLocation id) {
        return ITurtleUpgrade.createKey(id);
    }

}
