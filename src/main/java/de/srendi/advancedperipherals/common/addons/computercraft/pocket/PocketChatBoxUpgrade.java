package de.srendi.advancedperipherals.common.addons.computercraft.pocket;

import dan200.computercraft.api.pocket.IPocketAccess;
import dan200.computercraft.api.pocket.IPocketUpgrade;
import dan200.computercraft.api.upgrades.UpgradeType;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.ChatBoxPeripheral;
import de.srendi.advancedperipherals.common.setup.CCRegistration;
import de.srendi.advancedperipherals.lib.pocket.BasePocketUpgrade;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class PocketChatBoxUpgrade extends BasePocketUpgrade<ChatBoxPeripheral> {

    public PocketChatBoxUpgrade(ItemStack stack) {
        super(CCRegistration.ID.CHATTY_POCKET, stack);
    }

    @Nullable
    @Override
    public ChatBoxPeripheral getPeripheral(IPocketAccess access) {
        return new ChatBoxPeripheral(access);
    }

    @Override
    public UpgradeType<? extends IPocketUpgrade> getType() {
        return CCRegistration.CHAT_BOX_POCKET.get();
    }
}
