package de.srendi.advancedperipherals.testmod.tests

import dan200.computercraft.ingame.api.sequence
import net.minecraft.gametest.framework.GameTest
import net.minecraft.gametest.framework.GameTestHelper

class DummyTest {
    @GameTest
    fun passing(context: GameTestHelper) = context.sequence {
        this.thenExecute {

        }
    }
}