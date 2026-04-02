package de.srendi.advancedperipherals.client.smartglasses.objects;

public interface IObjectRenderer {
    /**
     * Get the weight of the renderer. Lower weight means higher priority and it will render first.
     * Some things need to be rendered before others to prevent color and opacity issues.
     * @return the weight of the renderer.
     */
    default int getWeight() {
        return 100;
    }
}
