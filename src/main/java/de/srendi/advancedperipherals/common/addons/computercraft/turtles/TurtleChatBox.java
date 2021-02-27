package de.srendi.advancedperipherals.common.addons.computercraft.turtles;

import dan200.computercraft.api.client.TransformedModel;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.ITurtleUpgrade;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.api.turtle.TurtleUpgradeType;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.ChatBoxPeripheral;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import de.srendi.advancedperipherals.common.setup.Blocks;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TurtleChatBox implements ITurtleUpgrade {

    private static final ModelResourceLocation leftModel = new ModelResourceLocation("computercraft:turtle_speaker_upgrade_left", "inventory");
    private static final ModelResourceLocation rightModel = new ModelResourceLocation("computercraft:turtle_speaker_upgrade_right", "inventory");

    @NotNull
    @Override
    public ResourceLocation getUpgradeID() {
        return new ResourceLocation(AdvancedPeripherals.MOD_ID, "chat_box_turtle");
    }

    @NotNull
    @Override
    public String getUnlocalisedAdjective() {
        return "block.advancedperipherals.chat_box_turtle";
    }

    @NotNull
    @Override
    public TurtleUpgradeType getType() {
        return TurtleUpgradeType.PERIPHERAL;
    }

    @NotNull
    @Override
    public ItemStack getCraftingItem() {
        if (AdvancedPeripheralsConfig.enableChatBox)
            return new ItemStack(Blocks.CHAT_BOX.get());
        return ItemStack.EMPTY;
    }

    @Nullable
    @Override
    public IPeripheral createPeripheral(@NotNull ITurtleAccess turtle, @NotNull TurtleSide side) {
        return new ChatBoxPeripheral("chatBox", null);
    }


    @NotNull
    @Override
    public TransformedModel getModel(@Nullable ITurtleAccess iTurtleAccess, @NotNull TurtleSide turtleSide) {
        return TransformedModel.of(turtleSide == TurtleSide.LEFT ? leftModel : rightModel);
    }

}
