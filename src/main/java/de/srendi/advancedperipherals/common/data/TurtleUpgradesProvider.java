package de.srendi.advancedperipherals.common.data;

import dan200.computercraft.api.turtle.ITurtleUpgrade;
import dan200.computercraft.api.turtle.TurtleUpgradeDataProvider;
import dan200.computercraft.api.turtle.TurtleUpgradeSerialiser;
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
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

public class TurtleUpgradesProvider extends TurtleUpgradeDataProvider {
    public TurtleUpgradesProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void addUpgrades(Consumer<Upgrade<TurtleUpgradeSerialiser<?>>> addUpgrade) {
        simpleWithCustomItem(CCRegistration.ID.Turtle.BLOCK_READER, CCRegistration.BLOCK_READER_TURTLE.get(), APBlocks.BLOCK_READER.get().asItem()).add(addUpgrade);
        simpleWithCustomItem(CCRegistration.ID.Turtle.CHATTY, CCRegistration.CHAT_BOX_TURTLE.get(), APBlocks.CHAT_BOX.get().asItem()).add(addUpgrade);
        simpleWithCustomItem(CCRegistration.ID.Turtle.CHUNKY, CCRegistration.CHUNKY_TURTLE.get(), APItems.CHUNK_CONTROLLER.get()).add(addUpgrade);
        simpleWithCustomItem(CCRegistration.ID.Turtle.COMPASS, CCRegistration.COMPASS_TURTLE.get(), Items.COMPASS).add(addUpgrade);
        simpleWithCustomItem(CCRegistration.ID.Turtle.END_AUTOMATA, CCRegistration.END_TURTLE.get(), APItems.END_AUTOMATA_CORE.get()).add(addUpgrade);
        simpleWithCustomItem(CCRegistration.ID.Turtle.ENVIRONMENT, CCRegistration.ENVIRONMENT_TURTLE.get(), APBlocks.ENVIRONMENT_DETECTOR.get().asItem()).add(addUpgrade);
        simpleWithCustomItem(CCRegistration.ID.Turtle.GEOSCANNER, CCRegistration.GEO_SCANNER_TURTLE.get(), APBlocks.GEO_SCANNER.get().asItem()).add(addUpgrade);
        simpleWithCustomItem(CCRegistration.ID.Turtle.HUSBANDRY_AUTOMATA, CCRegistration.HUSBANDRY_TURTLE.get(), APItems.HUSBANDRY_AUTOMATA_CORE.get()).add(addUpgrade);
        simpleWithCustomItem(CCRegistration.ID.Turtle.OP_END_AUTOMATA, CCRegistration.OP_END_TURTLE.get(), APItems.OVERPOWERED_END_AUTOMATA_CORE.get()).add(addUpgrade);
        simpleWithCustomItem(CCRegistration.ID.Turtle.OP_HUSBANDRY_AUTOMATA, CCRegistration.OP_HUSBANDRY_TURTLE.get(), APItems.OVERPOWERED_HUSBANDRY_AUTOMATA_CORE.get()).add(addUpgrade);
        simpleWithCustomItem(CCRegistration.ID.Turtle.OP_WEAK_AUTOMATA, CCRegistration.OP_WEAK_TURTLE.get(), APItems.OVERPOWERED_WEAK_AUTOMATA_CORE.get()).add(addUpgrade);
        simpleWithCustomItem(CCRegistration.ID.Turtle.PLAYER, CCRegistration.PLAYER_DETECTOR_TURTLE.get(), APBlocks.PLAYER_DETECTOR.get().asItem()).add(addUpgrade);
        simpleWithCustomItem(CCRegistration.ID.Turtle.SADDLE, CCRegistration.SADDLE_TURTLE.get(), Items.SADDLE).add(addUpgrade);
        simpleWithCustomItem(CCRegistration.ID.Turtle.WEAK_AUTOMATA, CCRegistration.WEAK_TURTLE.get(), APItems.WEAK_AUTOMATA_CORE.get()).add(addUpgrade);
    }
}
