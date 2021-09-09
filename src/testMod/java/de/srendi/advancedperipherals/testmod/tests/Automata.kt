package de.srendi.advancedperipherals.testmod.tests

import dan200.computercraft.ingame.api.GameTest
import dan200.computercraft.ingame.api.GameTestHelper
import dan200.computercraft.ingame.api.sequence
import dan200.computercraft.ingame.api.thenComputerOk

class Automata {
    @GameTest(timeoutTicks = 400, batch = "server3")
    fun weak(context: GameTestHelper) = context.sequence {
        thenComputerOk()
    }
}