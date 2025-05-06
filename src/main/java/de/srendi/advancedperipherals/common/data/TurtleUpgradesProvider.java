package de.srendi.advancedperipherals.common.data;

import dan200.computercraft.api.turtle.TurtleUpgradeDataProvider;
import dan200.computercraft.api.turtle.TurtleUpgradeSerialiser;
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
        simpleWithCustomItem(CCRegistration.ID.CHUNKY_TURTLE, CCRegistration.CHUNKY_TURTLE.get(), Items.CHUNK_CONTROLLER.get()).add(addUpgrade);
        simpleWithCustomItem(CCRegistration.ID.COMPASS_TURTLE, CCRegistration.COMPASS_TURTLE.get(), net.minecraft.world.item.Items.COMPASS).add(addUpgrade);

    }
}
