package siredvin.site.progressiveperipherals.testmod.tests

import com.mojang.blaze3d.matrix.MatrixStack
import dan200.computercraft.ingame.api.GameTest
import dan200.computercraft.ingame.api.GameTestHelper
import dan200.computercraft.ingame.api.sequence
import dan200.computercraft.ingame.api.thenOnClient
import de.srendi.advancedperipherals.AdvancedPeripherals
import net.minecraft.client.Minecraft
import net.minecraft.util.ResourceLocation
import vazkii.patchouli.api.PatchouliAPI
import vazkii.patchouli.client.book.gui.GuiBookEntry
import vazkii.patchouli.common.base.PatchouliConfig
import vazkii.patchouli.common.item.ItemModBook

val BOOK_ID = ResourceLocation("advancedperipherals:manual")

class PatchouliTest {
    @GameTest(batch = "client")
    fun book(context: GameTestHelper) = context.sequence {
        this.thenOnClient {
            val api = PatchouliAPI.get()
            val bookItem = api.getBookStack(BOOK_ID)
            val book = ItemModBook.getBook(bookItem)
            val mc = Minecraft.getInstance()
            PatchouliConfig.disableAdvancementLocking.set(true)
            book.reloadContentsAndExtensions()
            if (book.contents.isErrored)
                context.fail("Patchouli book render error")
            book.contents.getCurrentGui().init(mc, mc.window.guiScaledWidth, mc.window.guiScaledHeight)
            book.contents.openLexiconGui(book.contents.getCurrentGui(), false)
            for (entry in book.contents.entries) {
                AdvancedPeripherals.LOGGER.warn("Call render for %s entry".format(entry.value.id))
                val entryId = entry.key
                for (page in 0 until entry.value.pages.size) {
                    book.contents.checkValidCurrentEntry()
                    if (entryId != null) {
                        book.contents.setTopEntry(entryId, page)
                        if (book.contents.currentGui is GuiBookEntry) {
                            book.contents.currentGui.init(mc, mc.window.guiScaledWidth, mc.window.guiScaledHeight)
                            entry.value.pages[page].onDisplayed(book.contents.currentGui as GuiBookEntry, 0, 0)
                            entry.value.pages[page].render(MatrixStack(), 0, 0, 0.0f)
                        } else {
                            context.fail("Well, how this is happened?")
                        }
                    }
                }
            }
//            book.contents.getCurrentGui().onClose()
        }
    }
}