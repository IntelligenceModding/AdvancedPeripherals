---
--- Advanced Peripherals tests for the Botania integration on Flowers
--- Covers `getMana`, `getMaxMana`, `isFloating`,
--- `isOnEnchantedSoil`, `isEmpty`, `isFull`
---

-- Test for Entropinnyum (Flower has full mana store and is on enchanted soil)
test.eq("manaFlower", peripheral.getType("left"), "Peripheral should be manaFlower")
entropinnyum = peripheral.wrap("left")
test.assert(entropinnyum, "Peripheral not found")

test.eq(entropinnyum.getMaxMana(), entropinnyum.getMana(), "Entropinnyum should have full mana")
test.eq(6500, entropinnyum.getMaxMana(), "Entropinnyum should have a max mana of 6500")
-- test.assert(entropinnyum.isOnEnchantedSoil(), "Entropinnyum should be on enchanted soil") TODO: the function is currently broken
test.assert(not entropinnyum.isFloating(), "Entropinnyum should not be floating")
-- test.assert(entropinnyum.isFull(), "Entropinnyum should be full") TODO: uncomment for 1.20.1 AP versions
-- test.assert(not entropinnyum.isEmpty(), "Entropinnyum should not be empty") TODO: uncomment for 1.20.1 AP versions

-- Test for Endoflame (Flower has no mana stored and is on normal soil)
test.eq("manaFlower", peripheral.getType("right"), "Peripheral should be manaFlower")
endoflame = peripheral.wrap("right")
test.assert(endoflame, "Peripheral not found")

test.eq(0, endoflame.getMana(), "Endoflame should have no mana")
test.eq(300, endoflame.getMaxMana(), "Endoflame should have a max mana of 300")
-- test.assert(not endoflame.isOnEnchantedSoil(), "Endoflame should not be on enchanted soil") TODO: the function is currently broken
test.assert(not endoflame.isFloating(), "Endoflame should not be floating")
-- test.assert(not endoflame.isFull(), "Endoflame should not be full") TODO: uncomment for 1.20.1 AP versions
-- test.assert(endoflame.isEmpty(), "Endoflame should be empty") TODO: uncomment for 1.20.1 AP versions

-- Test for Kekimurus (Flower has 1800 mana stored and is floating)
test.eq("manaFlower", peripheral.getType("back"), "Peripheral should be manaFlower")
kekimurus = peripheral.wrap("back")
test.assert(kekimurus, "Peripheral not found")

test.eq(1800, kekimurus.getMana(), "Kekimurus should have 1800 mana")
test.eq(9001, kekimurus.getMaxMana(), "Kekimurus should have a max mana of 9001")
-- test.assert(not kekimurus.isOnEnchantedSoil(), "Kekimurus should not be on enchanted soil") TODO: the function is currently broken
test.assert(kekimurus.isFloating(), "Kekimurus should be floating")
-- test.assert(not kekimurus.isFull(), "Kekimurus should not be full") TODO: uncomment for 1.20.1 AP versions
-- test.assert(not kekimurus.isEmpty(), "Kekimurus should not be empty") TODO: uncomment for 1.20.1 AP versions
