package de.srendi.advancedperipherals.test

import dan200.computercraft.gametest.api.GameTestHolder
import dan200.computercraft.gametest.api.sequence
import de.srendi.advancedperipherals.common.setup.Blocks
import net.minecraft.core.BlockPos
import net.minecraft.gametest.framework.GameTest
import net.minecraft.gametest.framework.GameTestHelper

@GameTestHolder
class Peripheral_Test {

    @GameTest
    fun Environment(context: GameTestHelper) = context.sequence {
        val detector = BlockPos(2, 2, 2);
        thenExecute {
            context.assertBlock(detector, {block -> block.defaultBlockState().`is`(Blocks.ENVIRONMENT_DETECTOR.get())}, "Block is not a environment detector");
        }
    }

}