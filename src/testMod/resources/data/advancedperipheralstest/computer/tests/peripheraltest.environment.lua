detector = peripheral.find("environmentDetector")
test.eq(detector ~= nil, true, "Peripheral not found")

isRaining = detector.isRaining()
test.eq(false,isRaining, "It should not rain")
