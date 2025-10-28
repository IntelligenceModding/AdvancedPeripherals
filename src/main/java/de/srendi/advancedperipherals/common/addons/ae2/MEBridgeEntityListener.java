package de.srendi.advancedperipherals.common.addons.ae2;

import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridNodeListener;
import de.srendi.advancedperipherals.common.blocks.blockentities.MeBridgeEntity;

public class MEBridgeEntityListener implements IGridNodeListener<MeBridgeEntity> {

    public static final MEBridgeEntityListener INSTANCE = new MEBridgeEntityListener();

    @Override
    public void onSecurityBreak(MeBridgeEntity nodeOwner, IGridNode node) {
        // Maybe do something special with these methods?
    }

    @Override
    public void onSaveChanges(MeBridgeEntity nodeOwner, IGridNode node) {
        // Maybe do something special with these methods?
    }
}
