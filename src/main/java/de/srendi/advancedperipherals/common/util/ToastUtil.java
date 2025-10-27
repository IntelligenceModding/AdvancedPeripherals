package de.srendi.advancedperipherals.common.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;

/**
 * Outsourced toast utilities to prevent client class loading on the server side
 */
public class ToastUtil {

    /**
     * Displays a toast on a players screen.
     * This is a client side operation! Don't execute this on the server side
     *
     * @param title the title of the toast
     * @param message the message of the toast
     */
    public static void displayToast(Component title, Component message) {
        Minecraft minecraft = Minecraft.getInstance();

        minecraft.getToasts().addToast(SystemToast.multiline(minecraft,
                SystemToast.SystemToastId.PERIODIC_NOTIFICATION, title, message));
    }

}
