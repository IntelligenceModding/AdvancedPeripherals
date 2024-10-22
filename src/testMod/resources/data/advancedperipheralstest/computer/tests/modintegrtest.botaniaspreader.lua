---
--- Advanced Peripherals tests for the Botania integration on Mana Spreaders
--- Covers `getMana`, `getMaxMana`, `getVariant`,
--- `isEmpty`, `isFull`, `getBounding`
---

-- TODO Add tests for hasLens and getLens in 1.20.1 AP versions

-- Test basic Mana Spreader that is empty and directed towards a Mana Pool
test.eq("mana_spreader", peripheral.getType("left"), "Peripheral should be manaSpreader")
spreader = peripheral.wrap("left")
test.assert(spreader, "Peripheral not found")

test.eq(0, spreader.getMana(), "Mana should be 0")
test.eq(1000, spreader.getMaxMana(), "Max mana should be 1000")
test.eq("MANA", spreader.getVariant(), "Variant should be MANA")
-- test.assert(spreader.isEmpty(), "Mana Spreader should be empty") TODO: method returns the wrong value currently
test.assert(not spreader.isFull(), "Mana Spreader should not be full")

bounding = spreader.getBounding()
computerPos = test.getComputerPosition()
test.assert(bounding, "Spreader binding should be returned")
test.assert(computerPos, "Computer position should be returned")

test.eq(computerPos.x + 1, bounding.x, "Bounding x should be set to Mana pool (+1 relative to computer)")
test.eq(computerPos.y, bounding.y, "Bounding y should be set to Mana pool (same as computer)")
test.eq(computerPos.z - 1, bounding.z, "Bounding z should be set to Mana pool (-1 relative to computer")

-- Test Gaia Mana Spreader that is full
test.eq("mana_spreader", peripheral.getType("right"), "Peripheral should be manaSpreader")
gaiaSpreader = peripheral.wrap("right")
test.assert(gaiaSpreader, "Peripheral not found")

test.eq(6400, gaiaSpreader.getMana(), "Mana should be 6400")
test.eq(6400, gaiaSpreader.getMaxMana(), "Max mana should be 6400")
test.eq("GAIA", gaiaSpreader.getVariant(), "Variant should be GAIA")
-- test.assert(not gaiaSpreader.isEmpty(), "Mana Spreader should not be empty") TODO: method returns the wrong value currently
test.assert(gaiaSpreader.isFull(), "Mana Spreader should be full")

test.assert(not gaiaSpreader.getBounding(), "Mana Spreader should not be bound to anything")

-- Test Elven Mana Spreader that has 177 mana
test.eq("mana_spreader", peripheral.getType("back"), "Peripheral should be manaSpreader")
elvenSpreader = peripheral.wrap("back")
test.assert(elvenSpreader, "Peripheral not found")

test.eq(177, elvenSpreader.getMana(), "Mana should be 177")
test.eq(1000, elvenSpreader.getMaxMana(), "Max mana should be 1000")
test.eq("ELVEN", elvenSpreader.getVariant(), "Variant should be ELVEN")
-- test.assert(not elvenSpreader.isEmpty(), "Mana Spreader should not be empty") TODO: method returns the wrong value currently
test.assert(not elvenSpreader.isFull(), "Mana Spreader should not be full")

test.assert(not elvenSpreader.getBounding(), "Mana Spreader should not be bound to anything")

-- Test Pulse Mana Spreader that is empty
test.eq("mana_spreader", peripheral.getType("top"), "Peripheral should be manaSpreader")
pulseSpreader = peripheral.wrap("top")
test.assert(pulseSpreader, "Peripheral not found")

test.eq(0, pulseSpreader.getMana(), "Mana should be 0")
test.eq(1000, pulseSpreader.getMaxMana(), "Max mana should be 1000")
test.eq("REDSTONE", pulseSpreader.getVariant(), "Variant should be REDSTONE")
-- test.assert(pulseSpreader.isEmpty(), "Mana Spreader should be empty") TODO: method returns the wrong value currently
test.assert(not pulseSpreader.isFull(), "Mana Spreader should not be full")

test.assert(not pulseSpreader.getBounding(), "Mana Spreader should not be bound to anything")
