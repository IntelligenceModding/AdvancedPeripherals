package de.srendi.advancedperipherals.testmod.tests

import dan200.computercraft.ingame.api.GameTest
import dan200.computercraft.ingame.api.GameTestHelper
import dan200.computercraft.ingame.api.sequence
import dan200.computercraft.ingame.api.thenComputerOk
import de.srendi.advancedperipherals.common.addons.computercraft.operations.SphereOperation

class Peripherals {
    @GameTest(batch = "server3")
    fun geoscanner(context: GameTestHelper) = context.sequence {
        thenComputerOk()
    }
}