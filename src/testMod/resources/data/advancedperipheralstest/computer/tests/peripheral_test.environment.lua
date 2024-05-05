detector = peripheral.find("environmentDetector")
if (detector == nil) then
    test.fail("Peripheral not found")
end

isRaining = detector.isRaining()
test.eq(false,isRaining, "It should not rain")
