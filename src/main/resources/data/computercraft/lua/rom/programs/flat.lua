
print("The earth is flat, at least in minecraft!")

local power = settings.get("ap.power.to.turn.earth.flat")
settings.set("ap.power.to.turn.earth.flat", power + 1)
settings.save()

if power >= 100 and power % 10 == 0 then
	term.setTextColor(colors.yellow)
	term.write('> ')
	term.setTextColor(colors.white)
	term.setCursorBlink(true)
	sleep(1)
	term.setCursorBlink(false)
	printError('\nERR: power supply is low, entering power saving mode ...')
	peripheral.find('monitor', function(_, monitor)
		if monitor.isUltimate then
			for i = 0, 15 do
				local c = 2 ^ i
				local r, g, b = monitor.getPaletteColor(c)
				monitor.setPaletteColor(c, r, g, b, 0.11)
				sleep()
			end
		end
	end)
end
