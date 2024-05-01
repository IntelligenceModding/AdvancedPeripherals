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
package de.srendi.advancedperipherals.common.smartglasses;

import dan200.computercraft.core.computer.ComputerSide;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SmartGlassesSlot extends SlotItemHandler {

    public final SlotType slotType;
    private boolean isEnabled;

    public SmartGlassesSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, SlotType slotType) {
        super(itemHandler, index, xPosition, yPosition);
        this.slotType = slotType;
        this.isEnabled = slotType == SlotType.defaultType();
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    @Override
    public boolean isActive() {
        return isEnabled;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    public static ComputerSide indexToSide(int slot) {
        return switch (slot) {
            case 0 -> ComputerSide.TOP;
            case 1 -> ComputerSide.LEFT;
            case 2 -> ComputerSide.FRONT;
            case 3 -> ComputerSide.RIGHT;
            default -> ComputerSide.BOTTOM;
        };
    }

}
