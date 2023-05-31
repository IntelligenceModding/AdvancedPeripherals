package de.srendi.advancedperipherals.common.data;

import dan200.computercraft.api.pocket.PocketUpgradeDataProvider;
import dan200.computercraft.api.pocket.PocketUpgradeSerialiser;
import de.srendi.advancedperipherals.common.setup.Blocks;
import de.srendi.advancedperipherals.common.setup.CCRegistration;
import net.minecraft.data.PackOutput;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class PocketUpgradesProvider extends PocketUpgradeDataProvider {

    public PocketUpgradesProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void addUpgrades(@NotNull Consumer<Upgrade<PocketUpgradeSerialiser<?>>> addUpgrade) {
        simpleWithCustomItem(CCRegistration.ID.CHATTY_POCKET, CCRegistration.CHAT_BOX_POCKET.get(), Blocks.CHAT_BOX.get().asItem()).add(addUpgrade);
        simpleWithCustomItem(CCRegistration.ID.PLAYER_POCKET, CCRegistration.PLAYER_DETECTOR_POCKET.get(), Blocks.PLAYER_DETECTOR.get().asItem()).add(addUpgrade);
        simpleWithCustomItem(CCRegistration.ID.ENVIRONMENT_POCKET, CCRegistration.ENVIRONMENT_POCKET.get(), Blocks.ENVIRONMENT_DETECTOR.get().asItem()).add(addUpgrade);
        simpleWithCustomItem(CCRegistration.ID.GEOSCANNER_POCKET, CCRegistration.GEO_SCANNER_POCKET.get(), Blocks.GEO_SCANNER.get().asItem()).add(addUpgrade);
    }
}
