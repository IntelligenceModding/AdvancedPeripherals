package de.srendi.advancedperipherals.lib.peripherals.owner;

public class OperationAbilityTest {

    //Commented out until we port ttoolkit to 1.18
    /*@Test
    public void testCooldownLong() {
        DummyPeripheralOwner owner = new DummyPeripheralOwner();
        owner.attachOperation(DummyOperations.LONG);
        OperationAbility operationAbility = owner.getAbility(PeripheralOwnerAbility.OPERATION);
        assertNotNull(operationAbility);
        int abilityCooldown = operationAbility.getCurrentCooldown(DummyOperations.LONG);
        assertTrue(operationAbility.isOnCooldown(DummyOperations.LONG));
        assertTrue(abilityCooldown > 10_000);
        assertTrue(abilityCooldown < 20_001);
    }

    @Test
    public void testCooldownShort() {
        DummyPeripheralOwner owner = new DummyPeripheralOwner();
        assertTrue(LibConfig.initialCooldownSensetiveLevel > DummyOperations.SHORT.cooldown);
        owner.attachOperation(DummyOperations.LONG);
        OperationAbility operationAbility = owner.getAbility(PeripheralOwnerAbility.OPERATION);
        assertNotNull(operationAbility);
        int abilityCooldown = operationAbility.getCurrentCooldown(DummyOperations.SHORT);
        assertFalse(operationAbility.isOnCooldown(DummyOperations.SHORT));
        assertEquals(0, abilityCooldown);
    }

    @Test
    public void testCooldownWithoutInitialCooldown() {
        DummyPeripheralOwner owner = new DummyPeripheralOwner();
        LibConfig.setTestMode(true);
        owner.attachOperation(DummyOperations.LONG);
        OperationAbility operationAbility = owner.getAbility(PeripheralOwnerAbility.OPERATION);
        assertNotNull(operationAbility);
        int abilityCooldown = operationAbility.getCurrentCooldown(DummyOperations.LONG);
        assertFalse(operationAbility.isOnCooldown(DummyOperations.LONG));
        assertEquals(0, abilityCooldown);
        LibConfig.setTestMode(false);
    }

    public enum DummyOperations implements IPeripheralOperation<Object> {
        LONG(20_000), SHORT(3_000);

        private final int cooldown;

        DummyOperations(int cooldown) {
            this.cooldown = cooldown;
        }

        @Override
        public void addToConfig(ForgeConfigSpec.Builder builder) {
        }

        @Override
        public int getInitialCooldown() {
            return cooldown;
        }

        @Override
        public int getCooldown(Object context) {
            return cooldown;
        }

        @Override
        public int getCost(Object context) {
            return 3;
        }

        @Override
        public Map<String, Object> computerDescription() {
            return new HashMap<>();
        }
    }*/

}
