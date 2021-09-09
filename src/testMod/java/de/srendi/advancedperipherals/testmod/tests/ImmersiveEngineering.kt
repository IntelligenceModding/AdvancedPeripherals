package de.srendi.advancedperipherals.testmod.tests

import dan200.computercraft.ingame.api.GameTest
import dan200.computercraft.ingame.api.GameTestHelper
import dan200.computercraft.ingame.api.sequence
import dan200.computercraft.ingame.api.thenComputerOk

class ImmersiveEngineering {
    @GameTest(timeoutTicks = 400, batch = "server1")
    fun redprobe(context: GameTestHelper) = context.sequence {
        thenComputerOk()
    }

    @GameTest(timeoutTicks = 400, batch = "server1")
    fun redconnector(context: GameTestHelper) = context.sequence {
        thenComputerOk()
    }
}