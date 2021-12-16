package de.srendi.advancedperipherals.common.addons.computercraft.pocket;

import dan200.computercraft.api.pocket.IPocketAccess;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.ChatBoxPeripheral;
import de.srendi.advancedperipherals.lib.pocket.BasePocketUpgrade;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class PocketChatBoxUpgrade extends BasePocketUpgrade<ChatBoxPeripheral> {

    public PocketChatBoxUpgrade(ResourceLocation id, ItemStack stack) {
        super(id, stack);
    }

    @Nullable
    @Override
    public ChatBoxPeripheral getPeripheral(IPocketAccess access) {
        return new ChatBoxPeripheral(access);
    }

}
