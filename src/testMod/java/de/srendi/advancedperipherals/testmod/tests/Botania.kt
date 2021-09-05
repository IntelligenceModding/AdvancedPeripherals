package de.srendi.advancedperipherals.testmod.tests

import dan200.computercraft.ingame.api.GameTest
import dan200.computercraft.ingame.api.GameTestHelper
import dan200.computercraft.ingame.api.sequence
import dan200.computercraft.ingame.api.thenComputerOk

class Botania {
    @GameTest(timeoutTicks = 400, batch = "server2")
    fun manapool(context: GameTestHelper) = context.sequence {
        thenComputerOk()
    }

    @GameTest(timeoutTicks = 400, batch = "server2")
    fun manaflower(context: GameTestHelper) = context.sequence {
        thenComputerOk()
    }

    @GameTest(timeoutTicks = 400, batch = "server2")
    fun manaspreader(context: GameTestHelper) = context.sequence {
        thenComputerOk()
    }
}