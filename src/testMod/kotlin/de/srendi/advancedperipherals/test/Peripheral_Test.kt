package de.srendi.advancedperipherals.test

import dan200.computercraft.gametest.api.GameTestHolder
import dan200.computercraft.gametest.api.sequence
import dan200.computercraft.gametest.api.thenComputerOk
import net.minecraft.core.BlockPos
import net.minecraft.gametest.framework.GameTest
import net.minecraft.gametest.framework.GameTestHelper

@GameTestHolder
class Peripheral_Test {

    @GameTest
    fun Environment(context: GameTestHelper) = context.sequence {
        val detector = BlockPos(2, 2, 2);
        thenComputerOk()
    }

}