
settings.define("ap.patch.window", {
	description = "Enable window API patch for alpha palette support",
	type = "boolean",
	default = true,
})

-- Not an easter egg
settings.define("ap.power.to.turn.earth.flat", {
	type = "number",
	default = 0,
})

if settings.get("ap.patch.window") then
	dofile("rom/patches/advancedperipherals/window.lua")
end
