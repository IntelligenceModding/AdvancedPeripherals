package de.srendi.advancedperipherals.client.renderer;

import appeng.api.client.AEKeyRendering;
import de.srendi.advancedperipherals.common.addons.ae2.disk.AEDiskKey;
import de.srendi.advancedperipherals.common.addons.ae2.disk.AEDiskKeys;

public final class AERendererRegister {
    public static void register() {
        AEKeyRendering.register(AEDiskKeys.INSTANCE, AEDiskKey.class, new AEDiskKeyRenderer());
    }
}
