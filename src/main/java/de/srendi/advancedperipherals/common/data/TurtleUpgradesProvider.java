package de.srendi.advancedperipherals.common.data;

import dan200.computercraft.api.turtle.TurtleUpgradeDataProvider;
import dan200.computercraft.api.turtle.TurtleUpgradeSerialiser;
import de.srendi.advancedperipherals.common.setup.APBlocks;
import de.srendi.advancedperipherals.common.setup.APItems;
import de.srendi.advancedperipherals.common.setup.CCRegistration;
import net.minecraft.data.DataGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class TurtleUpgradesProvider extends TurtleUpgradeDataProvider {

    public TurtleUpgradesProvider(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void addUpgrades(@NotNull Consumer<Upgrade<TurtleUpgradeSerialiser<?>>> addUpgrade) {
        simpleWithCustomItem(CCRegistration.ID.CHATTY_TURTLE, CCRegistration.CHAT_BOX_TURTLE.get(), APBlocks.CHAT_BOX.get().asItem()).add(addUpgrade);
        simpleWithCustomItem(CCRegistration.ID.PLAYER_TURTLE, CCRegistration.PLAYER_DETECTOR_TURTLE.get(), APBlocks.PLAYER_DETECTOR.get().asItem()).add(addUpgrade);
        simpleWithCustomItem(CCRegistration.ID.ENVIRONMENT_TURTLE, CCRegistration.ENVIRONMENT_TURTLE.get(), APBlocks.ENVIRONMENT_DETECTOR.get().asItem()).add(addUpgrade);
        simpleWithCustomItem(CCRegistration.ID.CHUNKY_TURTLE, CCRegistration.CHUNKY_TURTLE.get(), APItems.CHUNK_CONTROLLER.get()).add(addUpgrade);
        simpleWithCustomItem(CCRegistration.ID.GEOSCANNER_TURTLE, CCRegistration.GEO_SCANNER_TURTLE.get(), APBlocks.GEO_SCANNER.get().asItem()).add(addUpgrade);
        simpleWithCustomItem(CCRegistration.ID.COMPASS_TURTLE, CCRegistration.COMPASS_TURTLE.get(), net.minecraft.world.item.Items.COMPASS).add(addUpgrade);
        simpleWithCustomItem(CCRegistration.ID.WEAK_AUTOMATA, CCRegistration.WEAK_TURTLE.get(), APItems.WEAK_AUTOMATA_CORE.get()).add(addUpgrade);
        simpleWithCustomItem(CCRegistration.ID.END_AUTOMATA, CCRegistration.END_TURTLE.get(), APItems.END_AUTOMATA_CORE.get()).add(addUpgrade);
        simpleWithCustomItem(CCRegistration.ID.HUSBANDRY_AUTOMATA, CCRegistration.HUSBANDRY_TURTLE.get(), APItems.HUSBANDRY_AUTOMATA_CORE.get()).add(addUpgrade);
        simpleWithCustomItem(CCRegistration.ID.OP_WEAK_AUTOMATA, CCRegistration.OP_WEAK_TURTLE.get(), APItems.OVERPOWERED_WEAK_AUTOMATA_CORE.get()).add(addUpgrade);
        simpleWithCustomItem(CCRegistration.ID.OP_END_AUTOMATA, CCRegistration.OP_END_TURTLE.get(), APItems.OVERPOWERED_END_AUTOMATA_CORE.get()).add(addUpgrade);
        simpleWithCustomItem(CCRegistration.ID.OP_HUSBANDRY_AUTOMATA, CCRegistration.OP_HUSBANDRY_TURTLE.get(), APItems.OVERPOWERED_HUSBANDRY_AUTOMATA_CORE.get()).add(addUpgrade);

    }
}
