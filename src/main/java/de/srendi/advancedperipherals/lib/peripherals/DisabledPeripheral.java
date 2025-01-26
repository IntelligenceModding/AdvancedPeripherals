package de.srendi.advancedperipherals.lib.peripherals;

import dan200.computercraft.api.pocket.IPocketAccess;
import dan200.computercraft.api.pocket.IPocketUpgrade;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.PocketPeripheralOwner;

public class DisabledPeripheral extends BasePeripheral<PocketPeripheralOwner> {
    public static final DisabledPeripheral INSTANCE = new DisabledPeripheral("disabledPeripheral", null, null);

    private DisabledPeripheral(String type, IPocketAccess access, IPocketUpgrade upgrade) {
        super(type, new PocketPeripheralOwner(access, upgrade));
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
