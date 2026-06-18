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

local function loadAllAPIs(folder)
	for _, f in ipairs(fs.list(folder)) do
		if f:match(".+%.lua") then
			local path = fs.combine(folder, f)
			if not fs.isDir(path) then
				os.loadAPI(path)
			end
		end
	end
end

if smartglasses then
	loadAllAPIs('/rom/apis/smartglasses')
end
