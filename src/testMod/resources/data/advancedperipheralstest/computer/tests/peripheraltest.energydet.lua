---
--- Advanced Peripherals Energy Detector tests
--- Covers `getTransferRate`, `setTransferRateLimit`, `getTransferRateLimit`
---

test.eq("energy_detector", peripheral.getType("right"), "Peripheral should be energyDetector")

det = peripheral.wrap("right")
det.setTransferRateLimit(0)
sleep(0.5)
test.eq(0, det.getTransferRate(), "Transfer Rate should be 0")
test.eq(0, det.getTransferRateLimit(), "Transfer Rate Limit should be 0")

det.setTransferRateLimit(100)
sleep(0.5)
test.eq(100, det.getTransferRate(), "Transfer Rate should be 100")
test.eq(100, det.getTransferRateLimit(), "Transfer Rate Limit should be 100")

det.setTransferRateLimit(4000000)
sleep(0.5)
test.eq(100000, det.getTransferRate(), "Transfer Rate should be 100000") -- Rate limit from the cables
test.eq(4000000, det.getTransferRateLimit(), "Transfer Rate Limit should be 4000000")
