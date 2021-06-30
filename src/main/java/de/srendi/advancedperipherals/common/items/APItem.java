package de.srendi.advancedperipherals.common.items;

import de.srendi.advancedperipherals.common.items.base.BaseItem;
import net.minecraft.util.text.ITextComponent;

import java.util.Optional;

public class APItem extends BaseItem {

    String turtleID;
    String pocketID;
    ITextComponent description;

    public APItem(Properties properties, String turtleID, String pocketID, ITextComponent description) {
        super(properties);
        this.turtleID = turtleID;
        this.pocketID = pocketID;
        this.description = description;
    }

    public APItem(String turtleID, String pocketID, ITextComponent description) {
        super();
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
