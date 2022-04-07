package de.srendi.advancedperipherals.common.addons.appliedenergistics;

import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridNodeListener;
import de.srendi.advancedperipherals.common.blocks.blockentities.MeBridgeEntity;

//TODO: Maybe do something special with these methods?
public class MeBridgeEntityListener implements IGridNodeListener<MeBridgeEntity> {

    public static final MeBridgeEntityListener INSTANCE = new MeBridgeEntityListener();

    @Override
    public void onSecurityBreak(MeBridgeEntity nodeOwner, IGridNode node) {

    }

    @Override
    public void onSaveChanges(MeBridgeEntity nodeOwner, IGridNode node) {

    }
}