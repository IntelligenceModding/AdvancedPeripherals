package de.srendi.advancedperipherals.common.addons.ae2;

import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridNodeListener;
import de.srendi.advancedperipherals.common.blocks.blockentities.MEBridgeEntity;

//TODO: Maybe do something special with these methods?
public class MEBridgeEntityListener implements IGridNodeListener<MEBridgeEntity> {

    public static final MEBridgeEntityListener INSTANCE = new MEBridgeEntityListener();

    @Override
    public void onSaveChanges(MEBridgeEntity nodeOwner, IGridNode node) {

    }
}
