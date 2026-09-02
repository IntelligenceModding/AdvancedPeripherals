package de.srendi.advancedperipherals.common.util.fakelevel;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;

public class FakeLevel extends ClientLevel {
    public FakeLevel(ClientPacketListener connection, ClientLevel level) {
        super(
            connection,
            level.getLevelData(),
            level.dimension(),
            level.dimensionTypeRegistration(),
            2,
            1,
            level.getProfilerSupplier(),
            null,
            false,
            0
        );
    }
}
