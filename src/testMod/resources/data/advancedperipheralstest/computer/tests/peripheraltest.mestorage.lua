sleep(4)
bridge = peripheral.wrap("bottom")
test.assert(bridge, "Peripheral not found")

isOnline = bridge.isConnected()
test.assert(isOnline, "Bridge is not connected/system is not online")

validEnergy = bridge.getEnergyUsage() > 24
test.assert(validEnergy, tostring(bridge.getEnergyUsage()) .. " Consumption does not seem right")
