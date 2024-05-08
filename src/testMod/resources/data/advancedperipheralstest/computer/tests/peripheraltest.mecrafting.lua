sleep(4)
bridge = peripheral.wrap("bottom")
test.assert(bridge, "Peripheral not found")

isOnline = bridge.isConnected()
test.assert(isOnline, "Bridge is not connected/system is not online")

validEnergy = bridge.getEnergyUsage() > 24
test.assert(validEnergy, tostring(bridge.getEnergyUsage()) .. " Consumption does not seem right")

stickFilter = {name="minecraft:stick"}
planksFilter = {name="minecraft:oak_planks"}
logFilter = {name="minecraft:oak_log"}

isItemCrafting = bridge.isItemCrafting(stickFilter)
test.assert(isItemCrafting == true, "There shouldn't be a crafting job")

-- Should be true. We have a pattern for it.
isItemCraftable = bridge.isItemCraftable(stickFilter)
test.assert(isItemCraftable == true, "Stick should be craftable")

-- Logs should not be craftable, we don't have a pattern for it
isItemCraftable = bridge.getItem(logFilter).isCraftable
-- We use == false here since the not keyword would also return true if the value is nil
test.assert(isItemCraftable == false, "Log should not be craftable")

-- Planks are craftable
isItemCraftable = bridge.getItem(planksFilter).isCraftable
test.assert(isItemCraftable == true, "Planks should be craftable")

