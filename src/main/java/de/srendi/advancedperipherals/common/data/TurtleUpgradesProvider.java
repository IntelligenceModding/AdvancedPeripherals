package de.srendi.advancedperipherals.common.data;

import dan200.computercraft.api.turtle.TurtleUpgradeDataProvider;
import dan200.computercraft.api.turtle.TurtleUpgradeSerialiser;
import de.srendi.advancedperipherals.common.addons.computercraft.turtles.*;
import de.srendi.advancedperipherals.common.addons.computercraft.turtles.metaphysics.*;
import de.srendi.advancedperipherals.common.setup.Blocks;
import de.srendi.advancedperipherals.common.setup.CCRegistration;
import de.srendi.advancedperipherals.common.setup.Items;
import net.minecraft.data.DataGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class TurtleUpgradesProvider extends TurtleUpgradeDataProvider {

    public TurtleUpgradesProvider(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void addUpgrades(@NotNull Consumer<Upgrade<TurtleUpgradeSerialiser<?>>> addUpgrade) {
        simpleWithCustomItem(TurtleChatBoxUpgrade.ID, CCRegistration.CHAT_BOX_TURTLE.get(), Blocks.CHAT_BOX.get().asItem()).add(addUpgrade);
        simpleWithCustomItem(TurtlePlayerDetectorUpgrade.ID, CCRegistration.PLAYER_DETECTOR_TURTLE.get(), Blocks.PLAYER_DETECTOR.get().asItem()).add(addUpgrade);
        simpleWithCustomItem(TurtleEnvironmentDetectorUpgrade.ID, CCRegistration.ENVIRONMENT_TURTLE.get(), Blocks.ENVIRONMENT_DETECTOR.get().asItem()).add(addUpgrade);
        simpleWithCustomItem(TurtleChunkyUpgrade.ID, CCRegistration.CHUNKY_TURTLE.get(), Items.CHUNK_CONTROLLER.get()).add(addUpgrade);
        simpleWithCustomItem(TurtleGeoScannerUpgrade.ID, CCRegistration.GEO_SCANNER_TURTLE.get(), Blocks.GEO_SCANNER.get().asItem()).add(addUpgrade);
        simpleWithCustomItem(WeakAutomata.ID, CCRegistration.WEAK_TURTLE.get(), Items.WEAK_AUTOMATA_CORE.get()).add(addUpgrade);
        simpleWithCustomItem(EndAutomata.ID, CCRegistration.END_TURTLE.get(), Items.END_AUTOMATA_CORE.get()).add(addUpgrade);
        simpleWithCustomItem(HusbandryAutomata.ID, CCRegistration.HUSBANDRY_TURTLE.get(), Items.HUSBANDRY_AUTOMATA_CORE.get()).add(addUpgrade);
        simpleWithCustomItem(OverpoweredWeakAutomata.ID, CCRegistration.OP_WEAK_TURTLE.get(), Items.OVERPOWERED_WEAK_AUTOMATA_CORE.get()).add(addUpgrade);
        simpleWithCustomItem(OverpoweredEndAutomata.ID, CCRegistration.OP_END_TURTLE.get(), Items.OVERPOWERED_END_AUTOMATA_CORE.get()).add(addUpgrade);
        simpleWithCustomItem(OverpoweredHusbandryAutomata.ID, CCRegistration.OP_HUSBANDRY_TURTLE.get(), Items.OVERPOWERED_HUSBANDRY_AUTOMATA_CORE.get()).add(addUpgrade);

    }
}
