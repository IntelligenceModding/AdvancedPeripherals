---
--- Advanced Peripherals Block Reader tests
--- Covers `getBlockName`, `getBlockData`, `getBlockStates`, `isTileEntity`
---

function tablelength(T)
  local count = 0
  for _ in pairs(T) do count = count + 1 end
  return count
end

-- Test Block Reader functions on a simple block
test.eq("blockReader", peripheral.getType("left"), "Peripheral should be blockReader")
simpleReader = peripheral.wrap("left")
test.assert(simpleReader, "Peripheral not found")

test.eq("minecraft:polished_andesite", simpleReader.getBlockName(), "Block Name should be polished_andesite")
test.eq(nil, simpleReader.getBlockData(), "Block Data should be nil")
test.eq(false, simpleReader.isTileEntity(), "Block should not be a TileEntity")
test.eq(0, tablelength(simpleReader.getBlockStates()), "Block State should be empty")

-- Test Block Reader functions on a stair block
test.eq("blockReader", peripheral.getType("back"), "Peripheral should be blockReader")
stairReader = peripheral.wrap("back")
test.assert(stairReader, "Peripheral not found")

test.eq("minecraft:polished_andesite_stairs", stairReader.getBlockName(), "Block Name should be polished_andesite")
test.eq(nil, stairReader.getBlockData(), "Block Data should be nil")
test.assert(not stairReader.isTileEntity(), "Block should not be a TileEntity")
test.eq(4, tablelength(stairReader.getBlockStates()), "Block State should not be empty")

test.eq("east", stairReader.getBlockStates()["facing"], "Stair Facing should be east")
test.eq("bottom", stairReader.getBlockStates()["half"], "Stair Half should be bottom")
test.eq("straight", stairReader.getBlockStates()["shape"], "Stair Shape should be straight")
test.assert(not stairReader.getBlockStates()["waterlogged"], "Stair Waterlogged should be false")

-- Test Block Reader functions on a sign block
test.eq("blockReader", peripheral.getType("right"), "Peripheral should be blockReader")
signReader = peripheral.wrap("right")
test.assert(signReader, "Peripheral not found")

test.eq("minecraft:oak_sign", signReader.getBlockName(), "Block Name should be polished_andesite")
test.neq(nil, signReader.getBlockData(), "Block Data should not be nil")
test.assert(signReader.isTileEntity(), "Block should be a TileEntity")
test.eq(2, tablelength(signReader.getBlockStates()), "Block State should not be empty")

test.eq(4, signReader.getBlockStates()["rotation"], "Sign Rotation should be 4")
test.assert(not signReader.getBlockStates()["waterlogged"], "Sign Waterlogged should be false")

test.eq("black", signReader.getBlockData()["Color"], "Sign Color should be black")
test.eq("{\"text\":\"this\"}", signReader.getBlockData()["Text1"], "Sign Text1 should be 'this'")
test.eq("{\"text\":\"is a\"}", signReader.getBlockData()["Text2"], "Sign Text2 should be 'is a'")
test.eq("{\"text\":\"test\"}", signReader.getBlockData()["Text3"], "Sign Text3 should be 'test'")
test.eq("{\"text\":\"sign\"}", signReader.getBlockData()["Text4"], "Sign Text4 should be 'sign'")
