package de.srendi.advancedperipherals.test

import dan200.computercraft.gametest.api.GameTestHolder
import dan200.computercraft.gametest.api.sequence
import dan200.computercraft.gametest.api.thenComputerOk
import net.minecraft.gametest.framework.GameTest
import net.minecraft.gametest.framework.GameTestHelper

@GameTestHolder
class ModIntegrTest {

    @GameTest
    fun botaniaFlower(context: GameTestHelper) = context.sequence {
        thenComputerOk();
    }

    @GameTest
    fun botaniaManaPool(context: GameTestHelper) = context.sequence {
        thenComputerOk();
    }

    @GameTest
    fun botaniaSpreader(context: GameTestHelper) = context.sequence {
        thenComputerOk();
    }

    @GameTest(timeoutTicks = 300)
    fun minecraftBeacon(context: GameTestHelper) = context.sequence {
        thenComputerOk();
    }

}