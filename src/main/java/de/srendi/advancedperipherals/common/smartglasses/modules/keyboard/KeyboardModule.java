package de.srendi.advancedperipherals.common.smartglasses.modules.keyboard;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.container.KeyboardContainer;
import de.srendi.advancedperipherals.common.items.KeyboardItem;
import de.srendi.advancedperipherals.common.network.toclient.KeyboardMouseCapturePacket;
import de.srendi.advancedperipherals.common.setup.APDataComponents;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesComputer;
import de.srendi.advancedperipherals.common.smartglasses.SmartGlassesSideAccess;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModule;
import de.srendi.advancedperipherals.common.smartglasses.modules.IModuleFunctions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public class KeyboardModule implements IModule {

    private final KeyboardItem keyboardItem;
    private final ItemStack keyboardItemStack;
    private boolean lastCaptureMouse = false;
    private volatile boolean captureMouse = false;

    public KeyboardModule(KeyboardItem keyboardItem, ItemStack stack) {
        this.keyboardItem = keyboardItem;
        this.keyboardItemStack = stack;
    }

    @Override
    public ResourceLocation getName() {
        return AdvancedPeripherals.getRL("keyboard");
    }

    @Nullable
    @Override
    public IModuleFunctions getFunctions(SmartGlassesSideAccess access) {
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
    public void tick(SmartGlassesSideAccess glasses) {
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
        if (!(glasses.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (!(player.containerMenu instanceof KeyboardContainer keyboard)) {
            return;
        }
        if (keyboard.getKeyboardItem() == this.keyboardItemStack) {
            player.closeContainer();
        }
    }

    public void openKeyboard(SmartGlassesSideAccess glasses) {
        if (!(glasses.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        SmartGlassesComputer computer = glasses.getComputer();
        ItemStack stack = computer.getStack();

        stack.set(APDataComponents.KEYBOARD_OPENED.get(), true);
        computer.queueEvent("keyboard_open");

        KeyboardItem keyboardItem = this.keyboardItem;
        player.openMenu(keyboardItem.createContainerWithComputer(player, stack, computer), buf -> keyboardItem.writeContainerData(player, stack, buf));
        boolean captureMouse = this.captureMouse;
        this.lastCaptureMouse = captureMouse;
        if (captureMouse) {
            PacketDistributor.sendToPlayer(player, new KeyboardMouseCapturePacket(true));
        }
    }
}
