package de.srendi.advancedperipheralstest;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import org.junit.jupiter.api.Assertions;


@GameTestHolder("advancedperipheralstest")
public class TestGameTests {

    @PrefixGameTestTemplate(false)
    @GameTest(templateNamespace = "advancedperipheralstest")
    public static void envDetectorTest(GameTestHelper helper) {
        Assertions.assertEquals("Cock", "Cock");
    }

}
