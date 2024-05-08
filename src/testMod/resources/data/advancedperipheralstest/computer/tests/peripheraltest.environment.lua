detector = peripheral.find("environmentDetector")
test.assert(detector, true, "Peripheral not found")

isRaining = detector.isRaining()
test.eq(false, isRaining, "It should not rain")
