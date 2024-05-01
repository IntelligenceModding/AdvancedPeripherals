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

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is required by {@link SlotInputHandler} to define which item is
 * valid for the slot.
 */
public class SlotCondition {

    private List<TagKey<?>> neededTags = new ArrayList<>();

    private List<Item> neededItems = new ArrayList<>();

    /**
     * Sets the valid tags for the item
     *
     * @param neededTags
     *            the tags which the item should have
     * @return the current instance
     */
    public SlotCondition setNeededTags(List<TagKey<?>> neededTags) {
        this.neededTags = neededTags;
        return this;
    }

    /**
     * Sets the valid items for the slot
     *
     * @param neededItems
     *            the items which the item should be
     * @return the current instance
     */
    public SlotCondition setNeededItems(List<Item> neededItems) {
        this.neededItems = neededItems;
        return this;
    }

    /**
     * Sets the valid item for the slot
     *
     * @param item
     *            the item which the item should be
     * @return the current instance
     */
    public SlotCondition setNeededItem(Item item) {
        neededItems.clear();
        neededItems.add(item);
        return this;
    }

    /**
     * Sets the valid tag for the item
     *
     * @param tag
     *            the tag which the item should have
     * @return the current instance
     */
    public SlotCondition setNeededTag(TagKey<?> tag) {
        neededTags.clear();
        neededTags.add(tag);
        return this;
    }

    /**
     * Checks if the stack is valid for the tags/items. Used in
     * {@link SlotInputHandler}
     *
     * @param stack
     *            the {@link ItemStack} which needs to be checked
     * @return returns true if the item is valid
     */
    public boolean isValid(ItemStack stack) {
        boolean valid = true;
        if (!neededTags.isEmpty()) {
            for (TagKey<?> tag : neededTags) {
                if (stack.getTags().noneMatch(tag::equals))
                    valid = false;
            }
        }
        if (!neededItems.isEmpty() && (!neededItems.contains(stack.getItem())))
            valid = false;

        return valid;
    }

}
