package de.srendi.advancedperipherals.test

import net.minecraft.client.Minecraft
import net.minecraft.gametest.framework.GameTestAssertException
import net.minecraft.gametest.framework.GameTestHelper
import net.minecraft.network.chat.Component
import java.util.function.Predicate

fun GameTestHelper.chatContains(filter: Predicate<Component>) = Minecraft.getInstance().gui.chat.allMessages.any { filter.test(it.content) }

fun GameTestHelper.assertChatContains(component: Component) {
    if (!chatContains { it == component }) {
        throw GameTestAssertException("Expected chat to contain $component")
    }
}

fun GameTestHelper.assertChatContains(filter: Predicate<Component>) {
    if (!chatContains(filter)) {
        throw GameTestAssertException("Expected chat to contain message matching filter")
    }
}

fun GameTestHelper.assertChatNotContains(component: Component) {
    if (chatContains { it == component }) {
        throw GameTestAssertException("Expected chat to not contain $component")
    }
}

fun GameTestHelper.assertChatNotContains(filter: Predicate<Component>) {
    if (chatContains(filter)) {
        throw GameTestAssertException("Expected chat to not contain message matching filter")
    }
}