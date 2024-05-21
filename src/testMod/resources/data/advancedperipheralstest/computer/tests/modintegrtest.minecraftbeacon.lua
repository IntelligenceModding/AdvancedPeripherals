---
--- Advanced Peripherals tests for the Minecraft integration on Beacons
--- Covers `getLevel`, `getPrimaryEffect`, `getSecondaryEffect`
---

-- Wait until the beacon structure is formed
sleep(4)

-- Test left beacon, level 4, primary and secondary effect set to haste
test.eq("beacon", peripheral.getType("left"), "Peripheral should be beacon")
beacon = peripheral.wrap("left")
test.assert(beacon, "Peripheral not found")

test.eq(4, beacon.getLevel(), "Level should be 4")
test.eq("effect.minecraft.haste", beacon.getPrimaryEffect(), "Primary effect should be haste")
test.eq("effect.minecraft.haste", beacon.getSecondaryEffect(), "Secondary effect should be haste")

-- Test right beacon, level 4, primary effect set to speed, secondary effect not set
test.eq("beacon", peripheral.getType("right"), "Peripheral should be beacon")
beacon = peripheral.wrap("right")
test.assert(beacon, "Peripheral not found")

test.eq(4, beacon.getLevel(), "Level should be 4")
test.eq("effect.minecraft.speed", beacon.getPrimaryEffect(), "Primary effect should be haste")
test.eq("none", beacon.getSecondaryEffect(), "Secondary effect should be none")

-- Test top beacon, level 0, primary and secondary effect not set
test.eq("beacon", peripheral.getType("top"), "Peripheral should be beacon")
beacon = peripheral.wrap("top")
test.assert(beacon, "Peripheral not found")

test.eq(0, beacon.getLevel(), "Level should be 0")
test.eq("none", beacon.getPrimaryEffect(), "Primary effect should be none")
test.eq("none", beacon.getSecondaryEffect(), "Secondary effect should be none")
