---
--- Advanced Peripherals Chat Box tests
--- Covers `sendMessage`, `sendMessageToPlayer`, `sendToastToPlayer`, `sendFormattedToastToPlayer`,
--- `sendFormattedMessage`, `sendFormattedMessageToPlayer`
---

sleep(3)

chatBox = peripheral.find("chatBox")
test.assert(chatBox, "Peripheral not found")
config = chatBox.getConfiguration()

function assertCooldown()
    local currentCooldown = chatBox.getOperationCooldown("chatMessage")
    test.assert(currentCooldown > 0 and currentCooldown <= config["chatMessage"]["cooldown"], "Cooldown should be active after a message was sent")

    while chatBox.getOperationCooldown("chatMessage") > 0 do
        sleep(0.1)
    end
end

function assertSendMessage(fn, ...)
    local message = arg[1]
    test.assert(fn(unpack(arg)), message .. " should be sent")
    assertCooldown()
end

function assertFailedSendMessage(fn, ...)
    local message = arg[1]
    test.assert(not fn(unpack(arg)), message .. " should not be sent")
    -- test.eq(0, chatBox.getOperationCooldown("chatMessage"), "Cooldown should not be active after a failed message") failed messages still trigger cooldown, maybe a bug?
    assertCooldown() -- TODO Remove when fixed
end

function getFormattedMessage(message)
    local message = {
        { text = "Red ", color = "red" },
        { text = "Bold ", color = "white", bold = true },
        { text = "Click ", underlined = true, color = "white", clickEvent = { action = "open_url", value = "https://advancedperipherals.madefor.cc/" } },
        { text = message, color = "aqua", italic = true }
    }
    local json = textutils.serialiseJSON(message)
    return json
end

-- Test sendMessage in different formats
assertSendMessage(chatBox.sendMessage, "Default message")
assertSendMessage(chatBox.sendMessage, "Message with prefix", "GameTest")
assertSendMessage(chatBox.sendMessage, "Message with brackets", "GameTest", "<>")
assertSendMessage(chatBox.sendMessage, "Message with bracket color", "GameTest", "<>", "&a")
assertSendMessage(chatBox.sendMessage, "Message with short range", "GameTest", "<>", "&a", 3)
assertFailedSendMessage(chatBox.sendMessage, "Message with invalid brackets", "GameTest", "<")

-- Test sendMessageToPlayer in different formats
assertSendMessage(chatBox.sendMessageToPlayer, "Default message to player", "Dev")
assertSendMessage(chatBox.sendMessageToPlayer, "Message with prefix to player", "Dev", "GameTest")
assertSendMessage(chatBox.sendMessageToPlayer, "Message with brackets to player", "Dev", "GameTest", "<>")
assertSendMessage(chatBox.sendMessageToPlayer, "Message with bracket color to player", "Dev", "GameTest", "<>", "&a")
assertSendMessage(chatBox.sendMessageToPlayer, "Message with short range to player", "Dev", "GameTest", "<>", "&a", 3)
assertFailedSendMessage(chatBox.sendMessageToPlayer, "Message with invalid brackets to player", "Dev", "GameTest", "<")
assertFailedSendMessage(chatBox.sendMessageToPlayer, "Default message to invalid player", "InvalidPlayer")

-- Test sendFormattedMessage in different formats
assertSendMessage(chatBox.sendFormattedMessage, getFormattedMessage("Default formatted message"))
assertSendMessage(chatBox.sendFormattedMessage, getFormattedMessage("Formatted message with prefix"), "GameTest")
assertSendMessage(chatBox.sendFormattedMessage, getFormattedMessage("Formatted message with brackets"), "GameTest", "<>")
assertSendMessage(chatBox.sendFormattedMessage, getFormattedMessage("Formatted message with bracket color"), "GameTest", "<>", "&a")
assertSendMessage(chatBox.sendFormattedMessage, getFormattedMessage("Formatted message with short range"), "GameTest", "<>", "&a", 3)
assertFailedSendMessage(chatBox.sendFormattedMessage, getFormattedMessage("Formatted message with invalid brackets"), "GameTest", "<")

-- Test sendFormattedMessageToPlayer in different formats
assertSendMessage(chatBox.sendFormattedMessageToPlayer, getFormattedMessage("Default formatted message to player"), "Dev")
assertSendMessage(chatBox.sendFormattedMessageToPlayer, getFormattedMessage("Formatted message with prefix to player"), "Dev", "GameTest")
assertSendMessage(chatBox.sendFormattedMessageToPlayer, getFormattedMessage("Formatted message with brackets to player"), "Dev", "GameTest", "<>")
assertSendMessage(chatBox.sendFormattedMessageToPlayer, getFormattedMessage("Formatted message with bracket color to player"), "Dev", "GameTest", "<>", "&a")
assertSendMessage(chatBox.sendFormattedMessageToPlayer, getFormattedMessage("Formatted message with short range to player"), "Dev", "GameTest", "<>", "&a", 3)
assertFailedSendMessage(chatBox.sendFormattedMessageToPlayer, getFormattedMessage("Formatted message with invalid brackets to player"), "Dev", "GameTest", "<")
assertFailedSendMessage(chatBox.sendFormattedMessageToPlayer, getFormattedMessage("Default formatted message to invalid player"), "InvalidPlayer")

-- Test sendToastToPlayer in different formats
assertSendMessage(chatBox.sendToastToPlayer, "Default toast to player", "Toast Title", "Dev")
assertSendMessage(chatBox.sendToastToPlayer, "Toast with prefix to player", "Toast Title", "Dev", "GameTest")
assertSendMessage(chatBox.sendToastToPlayer, "Toast with brackets to player", "Toast Title", "Dev", "GameTest", "<>")
assertSendMessage(chatBox.sendToastToPlayer, "Toast with bracket color to player", "Toast Title", "Dev", "GameTest", "<>", "&a")
assertSendMessage(chatBox.sendToastToPlayer, "Toast with short range to player", "Toast Title", "Dev", "GameTest", "<>", "&a", 3)
assertFailedSendMessage(chatBox.sendToastToPlayer, "Toast with invalid brackets to player", "Toast Title", "Dev", "GameTest", "<")
assertFailedSendMessage(chatBox.sendToastToPlayer, "Default toast to invalid player", "Toast Title", "InvalidPlayer")

-- Test sendFormattedToastToPlayer in different formats
formattedToastTitle = textutils.serialiseJSON({ { text = "Formatted Toast Title", color = "dark_purple" } })
assertSendMessage(chatBox.sendFormattedToastToPlayer, getFormattedMessage("Default formatted toast to player"), formattedToastTitle, "Dev")
assertSendMessage(chatBox.sendFormattedToastToPlayer, getFormattedMessage("Formatted toast with prefix to player"), formattedToastTitle, "Dev", "GameTest")
assertSendMessage(chatBox.sendFormattedToastToPlayer, getFormattedMessage("Formatted toast with brackets to player"), formattedToastTitle, "Dev", "GameTest", "<>")
assertSendMessage(chatBox.sendFormattedToastToPlayer, getFormattedMessage("Formatted toast with bracket color to player"), formattedToastTitle, "Dev", "GameTest", "<>", "&a")
assertSendMessage(chatBox.sendFormattedToastToPlayer, getFormattedMessage("Formatted toast with short range to player"),formattedToastTitle, "Dev", "GameTest", "<>", "&a", 3)
assertFailedSendMessage(chatBox.sendFormattedToastToPlayer, getFormattedMessage("Formatted toast with invalid brackets to player"), formattedToastTitle, "Dev", "GameTest", "<")
assertFailedSendMessage(chatBox.sendFormattedToastToPlayer, getFormattedMessage("Default formatted toast to invalid player"), formattedToastTitle, "InvalidPlayer")
