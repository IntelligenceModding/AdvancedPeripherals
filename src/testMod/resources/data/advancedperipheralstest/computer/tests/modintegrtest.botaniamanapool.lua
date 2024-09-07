---
--- Advanced Peripherals tests for the Botania integration on Mana Pools
--- Covers `getMana`, `getMaxMana`, `getManaNeeded`,
--- `isEmpty`, `isFull`
---

-- TODO Add tests for canChargeItem, hasItems and getItems in 1.20.1 AP versions

-- Test Fabulous Mana Pool (should be empty)
test.eq("mana_pool", peripheral.getType("left"), "Peripheral should be manaPool")
fabulous = peripheral.wrap("left")
test.assert(fabulous, "Peripheral not found")

test.eq(0, fabulous.getMana(), "Mana should be 0")
test.eq(1000000, fabulous.getMaxMana(), "Max mana should be 1000000")
test.eq(1000000, fabulous.getManaNeeded(), "Mana needed should be 1000000")
-- test.assert(fabulous.isEmpty(), "Mana pool should be empty") TODO method currently not implemented
test.assert(not fabulous.isFull(), "Mana pool should not be full")

-- Test Mana Pool (should have 36000 mana)
test.eq("mana_pool", peripheral.getType("right"), "Peripheral should be manaPool")
manaPool = peripheral.wrap("right")
test.assert(manaPool, "Peripheral not found")

test.eq(36000, manaPool.getMana(), "Mana should be 36000")
test.eq(1000000, manaPool.getMaxMana(), "Max mana should be 1000000")
test.eq(964000, manaPool.getManaNeeded(), "Mana needed should be 964000")
-- test.assert(not manaPool.isEmpty(), "Mana pool should not be empty") TODO method currently not implemented
test.assert(not manaPool.isFull(), "Mana pool should not be full")

-- Test Creative Mana Pool (should have 1000000 mana)
test.eq("mana_pool", peripheral.getType("back"), "Peripheral should be manaPool")
creative = peripheral.wrap("back")
test.assert(creative, "Peripheral not found")

test.eq(1000000, creative.getMana(), "Mana should be 1000000")
test.eq(1000000, creative.getMaxMana(), "Max mana should be 1000000")
test.eq(0, creative.getManaNeeded(), "Mana needed should be 0")
-- test.assert(not creative.isEmpty(), "Mana pool should not be empty") TODO method currently not implemented
test.assert(creative.isFull(), "Mana pool should be full")
