---
--- Advanced Peripherals ME Bridge transfer tests
--- Covers `isConnected`, `getEnergyUsage`, `getItem`, `exportItemToPeripheral`, `exportItem`,
--- `importItem`, `importItemFromPeripheral`
---

sleep(4)
bridge = peripheral.wrap("meBridge_3")
chest = peripheral.wrap("minecraft:chest_2")
test.assert(bridge, "Peripheral not found")
test.assert(chest, "Chest not found")

isOnline = bridge.isConnected()
test.assert(isOnline, "Bridge is not connected/system is not online")

energyUsage = bridge.getEnergyUsage()
test.assert(energyUsage > 17, tostring(energyUsage) .. " Consumption does not seem right")

planksFilter = {name="minecraft:oak_planks", count=5}
logFilter = {name="minecraft:oak_log", count=8}

exported, err = bridge.exportItemToPeripheral(planksFilter, peripheral.getName(chest))
test.assert(exported == 5, "Export failed ".. (err or ""))

planksItem = bridge.getItem(planksFilter)
test.assert(planksItem.amount == 251, "We should have 251 planks")

chestPlanks = nil
for slot, item in pairs(chest.list()) do
    if item.name == "minecraft:oak_planks" then
        chestPlanks = item
    end
end

test.assert(chestPlanks, "Planks not found in chest")
test.assert(chestPlanks.count == 5, "We should have 5 planks in the chest")

exported, err = bridge.exportItem(planksFilter, "front")
test.assert(exported == 5, "Export failed ".. (err or ""))

planksItem = bridge.getItem(planksFilter)
test.assert(planksItem.amount == 246, "We should have 246 planks")

for slot, item in pairs(chest.list()) do
    if item.name == "minecraft:oak_planks" then
        chestPlanks = item
    end
end

test.assert(chestPlanks, "Planks not found in chest")
test.assert(chestPlanks.count == 10, "We should have 5 planks in the chest")

imported, err = bridge.importItemFromPeripheral(logFilter, peripheral.getName(chest))
test.assert(imported == 8, "import failed ".. (err or ""))

logsItem = bridge.getItem(logFilter)
test.assert(logsItem.amount == 264, "We should have 264 logs")

chestLogs = nil
for slot, item in pairs(chest.list()) do
    if item.name == "minecraft:oak_log" then
        chestLogs = item
    end
end

test.assert(chestLogs, "Logs not found in chest")
test.assert(chestLogs.count == 56, "We should have 56 logs in the chest")

imported, err = bridge.importItem(logFilter, "south")
test.assert(imported == 8, "import failed ".. (err or ""))

logsItem = bridge.getItem(logFilter)
test.assert(logsItem.amount == 272, "We should have 272 logs")

chestLogs = nil
for slot, item in pairs(chest.list()) do
    if item.name == "minecraft:oak_log" then
        chestLogs = item
    end
end

test.assert(chestLogs, "Logs not found in chest")
test.assert(chestLogs.count == 48, "We should have 48 logs in the chest")