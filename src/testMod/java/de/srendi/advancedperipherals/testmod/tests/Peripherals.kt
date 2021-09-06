package de.srendi.advancedperipherals.testmod.tests

import dan200.computercraft.ingame.api.sequence
import dan200.computercraft.ingame.api.thenComputerOk
import net.minecraft.gametest.framework.GameTest
import net.minecraft.gametest.framework.GameTestHelper

class Peripherals {
    @GameTest
    fun geoscanner(context: GameTestHelper) = context.sequence {
        thenComputerOk()
    }
}