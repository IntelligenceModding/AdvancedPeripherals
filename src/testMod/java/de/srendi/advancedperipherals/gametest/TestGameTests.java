package de.srendi.advancedperipherals.gametest;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

@GameTestHolder("advancedperipheralstest")
public class TestGameTests {

    @PrefixGameTestTemplate(false)
    @GameTest(templateNamespace = "advancedperipheralstest")
    public static void envDetectorTest(GameTestHelper helper) {
        helper.succeed();
    }

}
