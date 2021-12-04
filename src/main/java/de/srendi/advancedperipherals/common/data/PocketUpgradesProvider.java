package de.srendi.advancedperipherals.common.data;

import dan200.computercraft.api.pocket.PocketUpgradeDataProvider;
import dan200.computercraft.api.pocket.PocketUpgradeSerialiser;
import de.srendi.advancedperipherals.common.addons.computercraft.pocket.*;
import de.srendi.advancedperipherals.common.setup.Blocks;
import de.srendi.advancedperipherals.common.setup.CCRegistration;
import net.minecraft.data.DataGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class PocketUpgradesProvider extends PocketUpgradeDataProvider {

    public PocketUpgradesProvider(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void addUpgrades(@NotNull Consumer<Upgrade<PocketUpgradeSerialiser<?>>> addUpgrade) {
        simpleWithCustomItem(PocketChatBoxUpgrade.ID, CCRegistration.CHAT_BOX_POCKET.get(), Blocks.CHAT_BOX.get().asItem()).add(addUpgrade);
        simpleWithCustomItem(PocketPlayerDetectorUpgrade.ID, CCRegistration.PLAYER_DETECTOR_POCKET.get(), Blocks.PLAYER_DETECTOR.get().asItem()).add(addUpgrade);
        simpleWithCustomItem(PocketEnvironmentUpgrade.ID, CCRegistration.ENVIRONMENT_POCKET.get(), Blocks.ENVIRONMENT_DETECTOR.get().asItem()).add(addUpgrade);
        simpleWithCustomItem(PocketGeoScannerUpgrade.ID, CCRegistration.GEO_SCANNER_POCKET.get(), Blocks.GEO_SCANNER.get().asItem()).add(addUpgrade);
    }
}
