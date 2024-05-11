---
--- Advanced Peripherals NBT Storage tests
--- Covers `read`, `writeJson`, `writeTable`
---

TEST_STRING = "Hello, World!"
TEST_NUMBER = 42
TEST_FLOAT = 3.14
TEST_VALUE = "AP Game Test"
TEST_JSON_VALUE = "AP Game Test JSON"

test.eq("nbtStorage", peripheral.getType("left"), "Peripheral should be nbtStorage")
storage = peripheral.wrap("left")
test.assert(storage, "Peripheral not found")

-- Read data from the test structure and verify it
stored = storage.read()
test.assert(stored, "Storage should not be nil")
test.eq(TEST_STRING, stored["test_string"], ("Stored string should be '%s'"):format(TEST_STRING))
test.eq(TEST_NUMBER, stored["test_number"], ("Stored number should be %d"):format(TEST_NUMBER))
test.eq(TEST_FLOAT, stored["test_float"], ("Stored float should be %f"):format(TEST_FLOAT))

-- Write a table to the storage and verify it
storage.writeTable({
    test_value = TEST_VALUE,
})

stored = storage.read()
test.assert(stored, "Storage should not be nil")
test.eq(TEST_VALUE, stored["test_value"], ("Stored value should be '%s'"):format(TEST_VALUE))

-- Write a JSON string to the storage and verify it
success = storage.writeJson(textutils.serializeJSON({test_value = TEST_JSON_VALUE}))
test.assert(success, "Storage writeJson should return true")

stored = storage.read()
test.assert(stored, "Storage should not be nil")
test.eq(TEST_JSON_VALUE, stored["test_value"], ("Stored value should be '%s'"):format(TEST_JSON_VALUE))
