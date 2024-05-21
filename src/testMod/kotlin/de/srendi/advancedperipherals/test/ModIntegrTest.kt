package de.srendi.advancedperipherals.test

import dan200.computercraft.gametest.api.*
import dan200.computercraft.gametest.core.TestEvents
import net.minecraft.core.BlockPos
import net.minecraft.gametest.framework.*
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.animal.allay.Allay
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

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

    @GameTest
    fun minecraftNoteBlock(context: GameTestHelper) = context.sequence {
        thenExecute { TestEvents.triggeredNoteBlocks.clear() }
        thenComputerOk()
        thenExecute {
            val successBlock = BlockPos(2, 2, 1)
            val failBlock = BlockPos(2, 2, 3)

            if (TestEvents.triggeredNoteBlocks.getOrDefault(context.absolutePos(successBlock), 0) != 1)
                context.fail("Note Block should have played one time", successBlock)

            if (TestEvents.triggeredNoteBlocks.getOrDefault(context.absolutePos(failBlock), 0) != 0)
                context.fail("Note Block should not have played", failBlock)
        }
    }

    @GameTest(timeoutTicks = 300)
    fun minecraftNoteBlock_Triggering_Allay(context: GameTestHelper) = context.sequence {
        // test if playNote triggers an allay
        // related issue: https://github.com/IntelligenceModding/AdvancedPeripherals/issues/603

        val item = Items.DIAMOND
        var allay: Allay? = null
        thenExecute {
            allay = context.spawn(EntityType.ALLAY, 2, 3, 2)
            allay?.setItemInHand(InteractionHand.MAIN_HAND, ItemStack(item))

            context.spawnItem(item, 2f, 3f, 2f)
        }

        thenWaitUntil { context.assertEntityNotPresent(EntityType.ITEM) }
        thenWaitUntil {
            if (allay?.inventory?.getItem(0)?.count != 1)
                context.fail("Expected Allay to pick up item")
        }
        thenOnComputer { callPeripheral("left", "playNote") }
        thenWaitUntil { context.assertEntityPresent(EntityType.ITEM) }
        thenWaitUntil {
            if (allay?.inventory?.getItem(0)?.count != 0)
                context.fail("Expected Allay to drop item")
        }
    }

}