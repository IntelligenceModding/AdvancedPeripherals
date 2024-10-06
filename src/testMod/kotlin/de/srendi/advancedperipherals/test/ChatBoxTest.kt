package de.srendi.advancedperipherals.test

import dan200.computercraft.gametest.api.*
import dan200.computercraft.gametest.api.Timeouts.SECOND
import dan200.computercraft.gametest.core.ClientTestEvents
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.toasts.SystemToast
import net.minecraft.core.BlockPos
import net.minecraft.gametest.framework.GameTestHelper
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import java.util.function.BiPredicate

@GameTestHolder
class ChatBoxTest {

    private fun getFormattedMessage(bracketColor: String, openBracket: Char, closeBracket: Char, prefix: String, message: String): Component {
        return Component.literal("$bracketColor$openBracket§r")
            .append(prefix)
            .append("$bracketColor$closeBracket§r ")
            .append(
                Component.literal("Red ").withStyle(ChatFormatting.RED)
                    .append(Component.literal("Bold ").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.WHITE))
                    .append(Component.literal("Click ").withStyle(ChatFormatting.UNDERLINE).withStyle(ChatFormatting.WHITE)
                            .withStyle { it.withClickEvent(ClickEvent(ClickEvent.Action.OPEN_URL, "https://advancedperipherals.madefor.cc/")) })
                    .append(Component.literal(message).withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.AQUA))
            )
    }

    private fun getFormattedToast(bracketColor: ChatFormatting?, openBracket: Char, closeBracket: Char, prefix: String, message: String): List<Component> {
        val prefixComponents = if (bracketColor != null) {
            listOf(
                Component.literal("$openBracket").withStyle(bracketColor),
                Component.literal(prefix),
                Component.literal("$closeBracket").withStyle(bracketColor),
                Component.literal(" ")
            )
        } else {
            listOf(
                Component.literal("$openBracket$prefix$closeBracket ")
            )
        }

        return prefixComponents + listOf(
            Component.literal("Red ").withStyle(ChatFormatting.RED),
            Component.literal("Bold ").withStyle(ChatFormatting.WHITE).withStyle(ChatFormatting.BOLD),
            Component.literal("Click ").withStyle(ChatFormatting.WHITE).withStyle(ChatFormatting.UNDERLINE)
                .withStyle { it.withClickEvent(ClickEvent(ClickEvent.Action.OPEN_URL, "https://advancedperipherals.madefor.cc/")) },
            Component.literal(message).withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.ITALIC)
        )
    }

    private fun containsToast(filter: BiPredicate<Component, List<Component>>): Boolean {
        return ClientTestEvents.toasts.filterIsInstance<SystemToast>().any {
            val sink = ComponentFormattedCharSink()
            it.messageLines.forEachIndexed { index, line ->
                line.accept(sink)
                if (index < it.messageLines.size - 1) {
                    sink.accept(0, sink.currentStyle!!, ' '.code)
                }
            }

            filter.test(it.title, sink.getComponents())
        }
    }

    private fun assertContainsToast(title: Component, components: List<Component>) {
        if (!containsToast { toastTitle, toastComponents -> toastTitle == title && toastComponents == components }) {
            throw AssertionError("Toast with title $title and components $components not found")
        }
    }

    private fun assertNotContainsToast(title: Component, components: List<Component>) {
        if (containsToast { toastTitle, toastComponents -> toastTitle == title && toastComponents == components }) {
            throw AssertionError("Toast with title $title and components $components found")
        }
    }

    private fun assertNotContainsToast(filter: BiPredicate<Component, List<Component>>) {
        if (containsToast(filter)) {
            throw AssertionError("Toast matching filter found")
        }
    }

    @ClientGameTest(timeoutTicks = 60 * SECOND)
    fun chatBox(context: GameTestHelper) = context.sequence {
        thenExecute { context.positionAt(BlockPos(2, 6, 2), 90f) }
        thenOnClient {
            Minecraft.getInstance().gui.chat.clearMessages(false)
            ClientTestEvents.toasts.clear()
        }
        thenComputerOk()

        thenOnClient {
            // sendMessage
            context.assertChatContains(Component.literal("[§r").append("AP").append("]§r ").append("Default message"))
            context.assertChatContains(Component.literal("[§r").append("GameTest").append("]§r ").append("Message with prefix"))
            context.assertChatContains(Component.literal("<§r").append("GameTest").append(">§r ").append("Message with brackets"))
            context.assertChatContains(Component.literal("§a<§r").append("GameTest").append("§a>§r ").append("Message with bracket color"))
            context.assertChatNotContains(Component.literal("§a<§r").append("GameTest").append("§a>§r ").append("Message with short range"))
            context.assertChatNotContains { it.toString().contains("Message with invalid brackets") }

            // sendMessageToPlayer
            context.assertChatContains(Component.literal("[§r").append("AP").append("]§r ").append("Default message to player"))
            context.assertChatContains(Component.literal("[§r").append("GameTest").append("]§r ").append("Message with prefix to player"))
            context.assertChatContains(Component.literal("<§r").append("GameTest").append(">§r ").append("Message with brackets to player"))
            context.assertChatContains(Component.literal("§a<§r").append("GameTest").append("§a>§r ").append("Message with bracket color to player"))
            context.assertChatNotContains(Component.literal("§a<§r").append("GameTest").append("§a>§r ").append("Message with short range to player"))
            context.assertChatNotContains { it.toString().contains("Message with invalid brackets to player") }
            context.assertChatNotContains(Component.literal("[§r").append("AP").append("]§r ").append("Default message to invalid player"))

            // sendFormattedMessage
            context.assertChatContains(getFormattedMessage("", '[', ']', "AP", "Default formatted message"))
            context.assertChatContains(getFormattedMessage("", '[', ']', "GameTest", "Formatted message with prefix"))
            context.assertChatContains(getFormattedMessage("", '<', '>', "GameTest", "Formatted message with brackets"))
            context.assertChatContains(getFormattedMessage("§a", '<', '>', "GameTest", "Formatted message with bracket color"))
            context.assertChatNotContains(getFormattedMessage("§a", '<', '>', "GameTest", "Formatted message with short range"))
            context.assertChatNotContains { it.toString().contains("Formatted message with invalid brackets") }

            // sendFormattedMessageToPlayer
            context.assertChatContains(getFormattedMessage("", '[', ']', "AP", "Default formatted message to player"))
            context.assertChatContains(getFormattedMessage("", '[', ']', "GameTest", "Formatted message with prefix to player"))
            context.assertChatContains(getFormattedMessage("", '<', '>', "GameTest", "Formatted message with brackets to player"))
            context.assertChatContains(getFormattedMessage("§a", '<', '>', "GameTest", "Formatted message with bracket color to player"))
            context.assertChatNotContains(getFormattedMessage("§a", '<', '>', "GameTest", "Formatted message with short range to player"))
            context.assertChatNotContains { it.toString().contains("Formatted message with invalid brackets to player") }
            context.assertChatNotContains(getFormattedMessage("", '[', ']', "AP", "Default formatted message to invalid player"))

            // sendToastToPlayer
            val defaultToastTitle = Component.literal("Toast Title")
            assertContainsToast(defaultToastTitle, listOf(Component.literal("[AP] Default toast to player")))
            assertContainsToast(defaultToastTitle, listOf(Component.literal("[GameTest] Toast with prefix to player")))
            assertContainsToast(defaultToastTitle, listOf(Component.literal("<GameTest> Toast with brackets to player")))
            assertContainsToast(defaultToastTitle, listOf(
                Component.literal("<").withStyle(ChatFormatting.GREEN),
                Component.literal("GameTest"),
                Component.literal(">").withStyle(ChatFormatting.GREEN),
                Component.literal(" Toast with bracket color to player")
            ))
            assertNotContainsToast(defaultToastTitle, listOf(
                Component.literal("<").withStyle(ChatFormatting.GREEN),
                Component.literal("GameTest"),
                Component.literal(">").withStyle(ChatFormatting.GREEN),
                Component.literal(" Toast with short range to player")
            ))
            assertNotContainsToast { title, components -> title == defaultToastTitle && components.any { it.toString().contains("Toast with invalid brackets to player") } }
            assertNotContainsToast(defaultToastTitle, listOf(Component.literal("[AP] Default toast to invalid player")))

            // sendFormattedToastToPlayer
            val formattedToastTitle = Component.literal("Formatted Toast Title").withStyle(ChatFormatting.DARK_PURPLE)
            assertContainsToast(formattedToastTitle, getFormattedToast(null, '[', ']', "AP", "Default formatted toast to player"))
            assertContainsToast(formattedToastTitle, getFormattedToast(null, '[', ']', "GameTest", "Formatted toast with prefix to player"))
            assertContainsToast(formattedToastTitle, getFormattedToast(null, '<', '>', "GameTest", "Formatted toast with brackets to player"))
            assertContainsToast(formattedToastTitle, getFormattedToast(ChatFormatting.GREEN, '<', '>', "GameTest", "Formatted toast with bracket color to player"))
            assertNotContainsToast(formattedToastTitle, getFormattedToast(ChatFormatting.GREEN, '<', '>', "GameTest", "Formatted toast with short range to player"))
            assertNotContainsToast { title, components -> title == formattedToastTitle && components.any { it.toString().contains("Formatted toast with invalid brackets to player") } }
            assertNotContainsToast(formattedToastTitle, getFormattedToast(null, '[', ']', "AP", "Default formatted toast to invalid player"))
        }
    }

    @ClientGameTest
    fun chatBox_Events(context: GameTestHelper) = context.sequence {
        thenIdle(20)
        thenOnClient { Minecraft.getInstance().player!!.chatSigned("This is a normal chat message", null) }
        thenIdle(20)
        thenOnClient { Minecraft.getInstance().player!!.chatSigned("\$This is a hidden chat message", null) }
        thenIdle(20)
        thenComputerOk()
    }

}