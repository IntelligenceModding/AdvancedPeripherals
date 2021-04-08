package de.srendi.advancedperipherals.common.addons.computercraft.pocket;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.pocket.AbstractPocketUpgrade;
import dan200.computercraft.api.pocket.IPocketAccess;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.ChatBoxPeripheral;
import de.srendi.advancedperipherals.common.setup.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PocketChatBox extends AbstractPocketUpgrade {

    private ChatBoxPeripheral peripheral;

    public PocketChatBox() {
        super(new ResourceLocation("advancedperipherals", "chatty_pocket"), "pocket.advancedperipherals.chatty_pocket", Blocks.ENVIRONMENT_DETECTOR);
    }

    @Nullable
    @Override
    public IPeripheral createPeripheral(@NotNull IPocketAccess iPocketAccess) {
        peripheral = new ChatBoxPeripheral("chatBox", iPocketAccess.getEntity());
        return peripheral;
    }

    @Override
    public boolean onRightClick(@NotNull World world, @NotNull IPocketAccess access, @Nullable IPeripheral peripheral) {
        if (access.getEntity() != null) {
            this.peripheral.setEntity(access.getEntity());
        }
        return false;
    }
}
