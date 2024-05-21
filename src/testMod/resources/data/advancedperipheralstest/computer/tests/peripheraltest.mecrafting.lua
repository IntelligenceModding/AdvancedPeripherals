---
--- Advanced Peripherals ME Bridge crafting tests
--- Covers `isConnected`, `getEnergyUsage`, `isItemCrafting`, `isItemCraftable`,
--- `getItem`, `craftItem`, `listCraftableFluid`, `craftFluid`, `getCraftingCPUs`,
---

sleep(4)
bridge = peripheral.wrap("bottom")
test.assert(bridge, "Peripheral not found")

isOnline = bridge.isConnected()
test.assert(isOnline, "Bridge is not connected/system is not online")

validEnergy = bridge.getEnergyUsage() > 24
test.assert(validEnergy, tostring(bridge.getEnergyUsage()) .. " Consumption does not seem right")

-- The count is used for the crafting filter
waterFilter = {name="minecraft:water"}
stickFilter = {name="minecraft:stick", count=5}
planksFilter = {name="minecraft:oak_planks"}
logFilter = {name="minecraft:oak_log"}

isItemCrafting = bridge.isItemCrafting(stickFilter)
test.assert(isItemCrafting == false, "There shouldn't be a crafting job")

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

stickCount = bridge.getItem(stickFilter).amount
test.assert(stickCount == 0, "We should not have sticks")

craftingSuccessful = bridge.craftItem(stickFilter)
test.assert(craftingSuccessful, "Crafting failed")

sleep(0.15)

isItemCrafting = bridge.isItemCrafting(stickFilter)
test.assert(isItemCrafting, "There should be a crafting job")
-- Wait for the crafting to finish
sleep(1)

-- A filter for 5 sticks should craft 8 sticks since we get 4 sticks per recipe
stickCount = bridge.getItem(stickFilter).amount
test.assert(stickCount == 8, "We should have 8 sticks")

-- There is no getFluid function, so we need to do it like this
waterCount = bridge.listCraftableFluid()[1].amount
test.assert(waterCount == 0, "We should not have water")

craftingSuccessful = bridge.craftFluid(waterFilter)
test.assert(craftingSuccessful, "Crafting failed")

-- We can't test if the amount of the water has increased since we don't have an actual way to craft water in vanilla.
-- We just have the pattern which uses a dummy recipe with one log to craft 1B water. The log ist just transferred to a chest

-- But we can test if there is a job
-- Well... if we would have a function for that...
-- isFluidCrafting = bridge.isItemCrafting(waterFilter)
-- test.assert(isFluidCrafting, "There should be a crafting job")

cpus = bridge.getCraftingCPUs()
test.assert(#cpus == 3, "There should be three CPUs")

cpuOne = nil
for i, cpu in ipairs(cpus) do
    if cpu.name == "CPUOne" then
        cpuOne = cpu
    end
end
test.assert(cpuOne.isBusy == false, "CPU 1 should not be busy")
test.assert(cpuOne.storage == 131072, "CPU 1 should have 131072 bytes of storage")
test.assert(cpuOne.coProcessors == 1, "CPU 1 should have one CO-CPU")
test.assert(cpuOne.name == "CPUOne", "CPU 1 name should be CPUOne")
test.assert(cpuOne.selectionMode == "PLAYER_ONLY", "CPU 1 selectionMode should be PLAYER_ONLY")

