package de.srendi.advancedperipherals.common.util.inventory;

@FunctionalInterface
public interface StorageProcessor<T> {
    /**
     * Process a storage content
     *
     * @param stack the original content, should not be modified
     * @return consumed amount.
     */
    int process(T stack);

    interface Large<T> {
        /**
         * Process a storage content
         *
         * @param stack the original content, should not be modified
         * @return consumed amount.
         */
        long process(T stack);
    }
}
