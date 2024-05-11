---
--- Advanced Peripherals Redstone Integrator tests
--- Covers `getInput`, `getOutput`, `getAnalogInput`,
--- `getAnalogOutput`, `setOutput`, `setAnalogOutput`
---

test.eq("redstoneIntegrator", peripheral.getType("left"), "Peripheral should be redstoneIntegrator")
test.eq("redstoneIntegrator", peripheral.getType("right"), "Peripheral should be redstoneIntegrator")

first = peripheral.wrap("left")
test.assert(first, "Peripheral not found")

second = peripheral.wrap("right")
test.assert(second, "Peripheral not found")

-- Test input for Redstone Block (full strength)
test.eq(15, second.getAnalogInput("back"), "Analog input should be 15 for Redstone Block")
test.assert(second.getInput("back"), "Digital input should be true for Redstone Block")

-- Test output on the right integrator
second.setOutput("front", true)
test.assert(second.getOutput("front"), "Digital output should be true")

-- Test analog input on the left integrator (wired from the right integrator)
test.eq(13, first.getAnalogInput("front"), "Analog input should be 13")

-- Test analog output on the right integrator
second.setAnalogOutput("front", 10)
test.eq(10, second.getAnalogOutput("front"), "Analog output should be 10")

-- Test analog input on the left integrator (wired from the right integrator)
test.eq(8, first.getAnalogInput("front"), "Analog input should be 8")

-- Reset redstone output
second.setOutput("front", false)
