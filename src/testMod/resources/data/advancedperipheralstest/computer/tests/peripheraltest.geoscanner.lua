---
--- Advanced Peripherals Geo Scanner tests
--- Covers `getFuelLevel`, `getMaxFuelLevel`, `cost`,
--- `scan`, `chunkAnalyze`, `getOperationCooldown`
---

function testBlockAt(result, x, y, z, expectedName, expectedTag)
    local blockEntry = nil
    for _, entry in ipairs(result) do
        if entry["x"] == x and entry["y"] == y and entry["z"] == z then
            blockEntry = entry
            break
        end
    end

    test.assert(blockEntry, ("Block at %d, %d, %d not found"):format(x, y, z))
    test.eq(expectedName, blockEntry["name"], ("Block at %d, %d, %d has the wrong name"):format(x, y, z))

    local tagFound = false
    for _, tag in ipairs(blockEntry["tags"]) do
        if tag == expectedTag then
            tagFound = true
            break
        end
    end
    test.assert(tagFound, ("Block at %d, %d, %d has the wrong tags"):format(x, y, z))
end

scanner = peripheral.find("geoScanner")
test.assert(scanner, "Peripheral not found")

config = scanner.getConfiguration()
fuelEnabled = scanner.getMaxFuelLevel() > 0

-- Test scan costs
test.eq(0, scanner.cost(1), "Scans with a range of 1 should be free")
test.eq(0, scanner.cost(config["scanBlocks"]["maxFreeRadius"]), "Scans with the maximum free radius should be free")
test.assert(scanner.cost(config["scanBlocks"]["maxFreeRadius"] + 1) > 0, "Scans with a radius larger than the maximum free radius should cost fuel")

test.assert(scanner.cost(config["scanBlocks"]["maxCostRadius"]) > 0, "Scans with the maximum cost radius should cost fuel")
test.assert(scanner.cost(config["scanBlocks"]["maxCostRadius"] + 1) == nil, "Scans with a radius larger than the maximum cost radius should not be possible")

-- Test scan results
scanResult = scanner.scan(1)
test.assert(scanResult, "Scan result should not be nil")

currentCooldown = scanner.getOperationCooldown("scanBlocks")
test.assert(currentCooldown > 0 and currentCooldown <= config["scanBlocks"]["cooldown"], "Cooldown should be active after a scan")

testBlockAt(scanResult, 0, 0, 0, "advancedperipherals:geo_scanner", "minecraft:block/minecraft:mineable/pickaxe")
testBlockAt(scanResult, 0, -1, 0, "computercraft:computer_advanced", "minecraft:block/minecraft:mineable/pickaxe")
testBlockAt(scanResult, 0, 1, 0, "minecraft:iron_ore", "minecraft:block/forge:ores/iron")
testBlockAt(scanResult, 0, -1, 1, "minecraft:polished_diorite", "minecraft:block/minecraft:mineable/pickaxe")
testBlockAt(scanResult, 0, -1, -1, "minecraft:polished_andesite", "minecraft:block/minecraft:mineable/pickaxe")
testBlockAt(scanResult, 1, -1, 0, "minecraft:polished_granite", "minecraft:block/minecraft:mineable/pickaxe")

while scanner.getOperationCooldown("scanBlocks") > 0 do
    sleep(0.25)
end

-- Test chunk analysis with ores
chunkResult = scanner.chunkAnalyze()

currentCooldown = scanner.getOperationCooldown("scanBlocks")
test.assert(currentCooldown > 0 and currentCooldown <= config["scanBlocks"]["cooldown"], "Cooldown should be active after a chunk analysis")

test.assert(chunkResult, "Chunk analysis result should not be nil")
test.eq(1, chunkResult["minecraft:iron_ore"], "Iron ore count should be 1")
test.eq(2, chunkResult["minecraft:gold_ore"], "Gold ore count should be 2")
test.eq(3, chunkResult["minecraft:diamond_ore"], "Diamond ore count should be 3")

while scanner.getOperationCooldown("scanBlocks") > 0 do
    sleep(0.25)
end

if fuelEnabled then
    scanResult = scanner.scan(config["scanBlocks"]["maxFreeRadius"] + 1)
    test.assert(scanResult, "Scan result should not be nil")
    test.eq(scanner.getMaxFuelLevel() - scanner.cost(config["scanBlocks"]["maxFreeRadius"] + 1), scanner.getFuelLevel(), "Fuel level should be reduced after a scan")
end
