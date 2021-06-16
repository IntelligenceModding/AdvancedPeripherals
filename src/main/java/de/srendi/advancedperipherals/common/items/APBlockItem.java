package de.srendi.advancedperipherals.common.items;

import de.srendi.advancedperipherals.common.items.base.BaseBlockItem;
import net.minecraft.block.Block;
import net.minecraft.util.text.ITextComponent;

import java.util.Optional;

public class APBlockItem extends BaseBlockItem {

    String turtleID;
    String pocketID;
    ITextComponent description;

    public APBlockItem(Block blockIn, Properties properties, String turtleID, String pocketID, ITextComponent description) {
        super(blockIn, properties);
        this.turtleID = turtleID;
        this.pocketID = pocketID;
        this.description = description;
    }

    public APBlockItem(Block blockIn, String turtleID, String pocketID, ITextComponent description) {
        super(blockIn);
        this.turtleID = turtleID;
        this.pocketID = pocketID;
        this.description = description;
    }

    @Override
    public Optional<String> getTurtleID() {
        return turtleID == null ? Optional.empty() : Optional.of(turtleID);
    }

    @Override
    public Optional<String> getPocketID() {
        return pocketID == null ? Optional.empty() : Optional.of(pocketID);
    }

    @Override
    public ITextComponent getDescription() {
        return description;
    }
}
