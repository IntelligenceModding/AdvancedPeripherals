package siredvin.site.progressiveperipherals.testmod.tests

import dan200.computercraft.ingame.api.GameTest
import dan200.computercraft.ingame.api.GameTestHelper
import dan200.computercraft.ingame.api.sequence
import dan200.computercraft.ingame.api.thenExecute

class DummyTest {
    @GameTest
    fun passing(context: GameTestHelper) = context.sequence {
        this.thenExecute {

        }
    }
}