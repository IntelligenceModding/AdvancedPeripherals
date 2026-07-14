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

local native = disk

for k, v in pairs(native) do
	_ENV[k] = v
end

function _ENV.getMountPath(name)
	local paths = table.pack(native.getMountPath(name))
	if paths[1] then
		return table.unpack(paths, 1, paths.n)
	end
	if peripheral.hasType(name, "multi_drive") then
		return table.unpack(peripheral.call(name, "getMountPaths"))
	end
	return nil
end
