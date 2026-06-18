-- Copyright 2026 The authors of AdvancePeripherals
--
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
--
--     http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.

local native = smartglasses

local MODULES_SIDE = 'back'

local function iterModules()
	local moduleNames = peripheral.call(MODULES_SIDE, 'getModules')
	local index = 1
	return function()
		local name = moduleNames[index]
		if not name then
			return nil
		end
		index = index + 1

		local module = peripheral.call(MODULES_SIDE, 'getModule', name)
		return name, module
	end
end

local function iterModulesAndAlias()
	local moduleNames = peripheral.call(MODULES_SIDE, 'getModules')
	local lastModule = nil
	local index = 1
	return function()
		if lastModule ~= nil then
			local module = lastModule
			lastModule = nil
			local alias = module.getAlias()
			if alias then
				return alias, module
			end
		end

		local name = moduleNames[index]
		if not name then
			return nil
		end
		index = index + 1

		local module = peripheral.call(MODULES_SIDE, 'getModule', name)
		lastModule = module
		return name, module
	end
end

local aliasCache = {}

_ENV.modules = setmetatable({}, {
	__pairs = iterModulesAndAlias,
	__index = function(t, name)
		if type(name) ~= 'string' then
			return nil
		end

		local mapped = aliasCache[name]
		if mapped then
			name = mapped
		end

		local module = peripheral.call(MODULES_SIDE, 'getModule', name)
		if module then
			return module
		end

		-- check alias
		for id, module in iterModules() do
			local alias = module.getAlias()
			if alias and name == alias then
				aliasCache[name] = id
				return module
			end
		end
		return nil
	end,
	__newindex = function(t, name)
		error('should not modify smartglasses.modules object', 2)
	end,
})

for k, v in pairs(native) do
	_ENV[k] = v
end
