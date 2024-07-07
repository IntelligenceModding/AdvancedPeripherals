---
--- Advanced Peripherals Chat Box tests
--- Covers `sendMessage`, `sendMessageToPlayer`, `sendToastToPlayer`, `sendFormattedToastToPlayer`,
--- `sendFormattedMessage`, `sendFormattedMessageToPlayer`
---

chatBox = peripheral.find("chatBox")
test.assert(chatBox, "Peripheral not found")

function assertMessage(msg, hidden)
    local event, username, message, uuid, isHidden = os.pullEvent("chat")
    test.eq("chat", event, "Event should be 'chat'")
    test.eq("Dev", username, "Username of sender should be 'Dev'")
    test.eq(msg, message, "Message should be '" .. msg .. "'")
    test.eq(hidden, isHidden, "Message should be " .. (hidden and "hidden" or "visible"))
end

assertMessage("This is a normal chat message", false)
assertMessage("This is a hidden chat message", true)
