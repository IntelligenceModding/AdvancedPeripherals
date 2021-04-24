package de.srendi.advancedperipherals.common.items;

import de.srendi.advancedperipherals.common.items.base.BaseBlockItem;
import de.srendi.advancedperipherals.common.setup.Blocks;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Optional;

public class ChatBoxItem extends BaseBlockItem {

    public ChatBoxItem() {
        super(Blocks.CHAT_BOX.get());
    }

    @Override
    protected Optional<String> getTurtleID() {
        return Optional.of("chat_box_turtle");
    }

    @Override
    protected Optional<String> getPocketID() {
        return Optional.of("chatty_turtle");
    }

    @Override
    protected ITextComponent getDescription() {
        return new TranslationTextComponent("item.advancedperipherals.tooltip.chat_box");
    }
}
