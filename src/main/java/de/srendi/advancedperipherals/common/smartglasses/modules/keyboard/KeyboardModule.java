package de.srendi.advancedperipherals.common.smartglasses.modules.keyboard;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.network.APNetworking;
import de.srendi.advancedperipherals.common.network.toclient.KeyboardMouseCapturePacket;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesAccess;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModuleFunctions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

public class KeyboardModule implements IModule {

    private boolean lastCaptureMouse = false;
    private volatile boolean captureMouse = false;

    @Override
    public ResourceLocation getName() {
        return AdvancedPeripherals.getRL("keyboard");
    }

    @Nullable
    @Override
    public IModuleFunctions getFunctions(SmartGlassesAccess smartGlassesAccess) {
        return new KeyboardFunctions(this);
    }

    public boolean isCapturingMouse() {
        return captureMouse;
    }

    public void setCaptureMouse(boolean enable) {
        if (captureMouse == enable) {
            return;
        }
        captureMouse = enable;
    }

    @Override
    public void tick(SmartGlassesAccess glasses) {
        if (!(glasses.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        boolean captureMouse = this.captureMouse;
        if (captureMouse != lastCaptureMouse) {
            lastCaptureMouse = captureMouse;
            APNetworking.sendTo(new KeyboardMouseCapturePacket(captureMouse), player);
        }
    }
}
