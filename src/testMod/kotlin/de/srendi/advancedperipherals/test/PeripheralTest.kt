package de.srendi.advancedperipherals.test

import dan200.computercraft.gametest.api.GameTestHolder
import dan200.computercraft.gametest.api.sequence
import dan200.computercraft.gametest.api.thenComputerOk
import net.minecraft.gametest.framework.GameTest
import net.minecraft.gametest.framework.GameTestHelper

@GameTestHolder
class PeripheralTest {

    @GameTest
    fun environment(context: GameTestHelper) = context.sequence {
        context.level.setWeatherParameters(6000, 0, false, false);
        thenComputerOk();
    }

    // The ME System needs to boot up, so we use a sleep() in the specific lua script
    // We set the timeoutTicks to 300 so the test does not fail
    @GameTest(timeoutTicks = 300)
    fun meCrafting(context: GameTestHelper) = context.sequence {
        thenComputerOk();
    }

    @GameTest(timeoutTicks = 300)
    fun meStorage(context: GameTestHelper) = context.sequence {
        thenComputerOk();
    }

}