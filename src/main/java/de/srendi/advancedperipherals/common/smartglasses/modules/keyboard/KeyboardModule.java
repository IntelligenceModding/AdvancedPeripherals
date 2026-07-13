package de.srendi.advancedperipherals.common.smartglasses.modules.keyboard;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.container.KeyboardContainer;
import de.srendi.advancedperipherals.common.items.KeyboardItem;
import de.srendi.advancedperipherals.common.network.toclient.KeyboardMouseCapturePacket;
import de.srendi.advancedperipherals.common.setup.APDataComponents;
import de.srendi.advancedperipherals.common.setup.CCEvents;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesComputer;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesSideAccess;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModuleFunctions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

public class KeyboardModule implements IModule {
    public static final ResourceLocation ID = AdvancedPeripherals.getRL("keyboard");

    private final KeyboardItem keyboardItem;
    private volatile boolean capturingKeys = false;
    private boolean lastCaptureMouse = false;
    private volatile boolean captureMouse = false;

    public KeyboardModule(KeyboardItem keyboardItem) {
        this.keyboardItem = keyboardItem;
    }

    @Override
    @NotNull
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public String getLuaAlias() {
        return "keyboard";
    }

    @Override
    @NotNull
    public IModuleFunctions getFunctions(SmartGlassesSideAccess access) {
        access.getComputer().setModuleData(APDataComponents.HANDLING_INTERACTION_BUTTONS.get(), null);
        return new KeyboardFunctions(this, access);
    }

    public boolean isCapturingKeys() {
        return this.capturingKeys;
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
    public void serverTick(SmartGlassesSideAccess glasses) {
        if (!(glasses.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        boolean captureMouse = this.captureMouse;
        if (captureMouse != lastCaptureMouse) {
            lastCaptureMouse = captureMouse;
            PacketDistributor.sendToPlayer(player, new KeyboardMouseCapturePacket(captureMouse));
        }
    }

    @Override
    public void onUnequipped(SmartGlassesSideAccess glasses) {
        glasses.getComputer().setModuleData(APDataComponents.HANDLING_INTERACTION_BUTTONS.get(), null);
        if (!(glasses.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (!(player.containerMenu instanceof KeyboardContainer)) {
            return;
        }
        player.closeContainer();
        this.onKeyboardClosed(glasses);
    }

    public void openKeyboard(SmartGlassesSideAccess glasses) {
        if (!(glasses.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        this.capturingKeys = true;

        SmartGlassesComputer computer = glasses.getComputer();
        if (!computer.isOn()) {
            computer.turnOn();
        }

        KeyboardItem keyboardItem = this.keyboardItem;
        player.openMenu(keyboardItem.createContainerWithModule(glasses, this));
        boolean captureMouse = this.captureMouse;
        this.lastCaptureMouse = captureMouse;

        computer.queueEvent(CCEvents.KEYBOARD_OPEN);

        if (captureMouse) {
            PacketDistributor.sendToPlayer(player, new KeyboardMouseCapturePacket(true));
        }
    }

    public void onKeyboardClosed(SmartGlassesSideAccess glasses) {
        this.capturingKeys = false;
        glasses.getComputer().queueEvent(CCEvents.KEYBOARD_CLOSE);
    }
}
