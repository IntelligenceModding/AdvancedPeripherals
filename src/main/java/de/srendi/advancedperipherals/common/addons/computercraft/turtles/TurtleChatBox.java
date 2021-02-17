package de.srendi.advancedperipherals.common.addons.computercraft.turtles;

import dan200.computercraft.api.client.TransformedModel;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.AbstractTurtleUpgrade;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.api.turtle.TurtleUpgradeType;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.ChatBoxPeripheral;
import de.srendi.advancedperipherals.common.setup.ModBlocks;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TurtleChatBox extends AbstractTurtleUpgrade {

    private static final ModelResourceLocation leftModel = new ModelResourceLocation( "computercraft:turtle_speaker_upgrade_left", "inventory" );
    private static final ModelResourceLocation rightModel = new ModelResourceLocation( "computercraft:turtle_speaker_upgrade_right", "inventory" );

    @Nullable
    @Override
    public IPeripheral createPeripheral(@NotNull ITurtleAccess turtle, @NotNull TurtleSide side) {
        return new ChatBoxPeripheral("chatBox", null);
    }

    public TurtleChatBox(ResourceLocation id) {
        super(id, TurtleUpgradeType.PERIPHERAL, ModBlocks.CHAT_BOX);
    }

    @NotNull
    @Override
    public TransformedModel getModel(@Nullable ITurtleAccess iTurtleAccess, @NotNull TurtleSide turtleSide) {
        return TransformedModel.of(turtleSide == TurtleSide.LEFT ? leftModel : rightModel);
    }

}
