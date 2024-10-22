package de.srendi.advancedperipherals.tests.utiltests;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.util.TranslationUtil;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TranslationAndStringTests {

    @Test
    void translationTests() {
        assertEquals("pocket.advancedperipherals.chatty_pocket", TranslationUtil.pocket("chatty_pocket"));

        assertEquals("turtle.advancedperipherals.husbandry_automata", TranslationUtil.turtle("husbandry_automata"));

        String descriptionId = Util.makeDescriptionId("item", new ResourceLocation(AdvancedPeripherals.MOD_ID, "peripheral_casing"));
        assertEquals("item.advancedperipherals.tooltip.peripheral_casing", TranslationUtil.itemTooltip(descriptionId).getString());
    }

}
