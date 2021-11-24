package de.srendi.advancedperipherals.common.addons.appliedenergistics;

import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridNodeListener;
import de.srendi.advancedperipherals.common.blocks.tileentity.MeBridgeTile;

//TODO: Maybe do something special with these methods?
public class MeBridgeEntityListener implements IGridNodeListener<MeBridgeTile> {

    public static final MeBridgeEntityListener INSTANCE = new MeBridgeEntityListener();

    @Override
    public void onSecurityBreak(MeBridgeTile nodeOwner, IGridNode node) {

    }

    @Override
    public void onSaveChanges(MeBridgeTile nodeOwner, IGridNode node) {

    }
}
