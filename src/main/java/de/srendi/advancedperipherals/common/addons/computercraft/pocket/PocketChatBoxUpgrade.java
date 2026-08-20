package de.srendi.advancedperipherals.common.addons.computercraft.pocket;

import dan200.computercraft.api.pocket.IPocketAccess;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.ChatBoxPeripheral;
import de.srendi.advancedperipherals.common.setup.CCRegistration;
import de.srendi.advancedperipherals.lib.pocket.BasePocketUpgrade;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PocketChatBoxUpgrade extends BasePocketUpgrade<ChatBoxPeripheral> {

    public PocketChatBoxUpgrade(ItemStack stack) {
        super(CCRegistration.ID.Pocket.CHATTY, stack);
    }

    @Override
    @NotNull
    protected ChatBoxPeripheral buildPeripheral(@NotNull IPocketAccess access) {
        return new ChatBoxPeripheral(access);
    }
}
