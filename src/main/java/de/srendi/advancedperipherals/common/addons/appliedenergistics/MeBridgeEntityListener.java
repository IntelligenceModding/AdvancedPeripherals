/*
 *     Copyright 2024 Intelligence Modding @ https://intelligence-modding.de
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.srendi.advancedperipherals.common.addons.appliedenergistics;

import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridNodeListener;
import de.srendi.advancedperipherals.common.blocks.blockentities.MeBridgeEntity;

public class MeBridgeEntityListener implements IGridNodeListener<MeBridgeEntity> {

    public static final MeBridgeEntityListener INSTANCE = new MeBridgeEntityListener();

    @Override
    public void onSecurityBreak(MeBridgeEntity nodeOwner, IGridNode node) {
        // Maybe do something special with these methods?
    }

    @Override
    public void onSaveChanges(MeBridgeEntity nodeOwner, IGridNode node) {
        // Maybe do something special with these methods?
    }
}
