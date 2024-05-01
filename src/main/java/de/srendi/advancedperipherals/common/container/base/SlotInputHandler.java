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
package de.srendi.advancedperipherals.common.container.base;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class SlotInputHandler extends SlotItemHandler {

    SlotCondition condition;

    public SlotInputHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition,
            SlotCondition condition) {
        super(itemHandler, index, xPosition, yPosition);
        this.condition = condition;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return condition.isValid(stack);
    }
}
