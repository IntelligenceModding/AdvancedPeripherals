
local native = advancedperipherals

local env = _ENV

env.INVENTORY_SIZE = 36
env.INVENTORY_ARMOR_OFFSET = env.INVENTORY_SIZE
env.INVENTORY_OFFHAND_SLOT = 40

for k, v in pairs(native) do
	env[k] = v
end
