package de.srendi.advancedperipherals.common.items;

import de.srendi.advancedperipherals.common.items.base.BaseBlockItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;

import java.util.Optional;
import java.util.function.Supplier;

public class APBlockItem extends BaseBlockItem {

    String turtleID;
    String pocketID;
    Component description;
    Supplier<Boolean> enabledSup;

    public APBlockItem(Block blockIn, Properties properties, String turtleID, String pocketID, Component description, Supplier<Boolean> enabledSup) {
        super(blockIn, properties);
        this.turtleID = turtleID;
        this.pocketID = pocketID;
        this.description = description;
        this.enabledSup = enabledSup;
    }

    public APBlockItem(Block blockIn, String turtleID, String pocketID, Component description, Supplier<Boolean> enabledSup) {
        super(blockIn);
        this.turtleID = turtleID;
        this.pocketID = pocketID;
        this.description = description;
        this.enabledSup = enabledSup;
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
    public Component getDescription() {
        return description;
    }

    @Override
    public boolean isEnabled() {
        return enabledSup.get();
    }
}
