/*
 *     Copyright 2024 Intelligence Modding @ https://intelligence-modding.de
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.srendi.advancedperipherals.common.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;

/**
 * Outsourced toast utilities to prevent client class loading on the server side
 */
public class ToastUtil {

    /**
     * Displays a toast on a players screen. This is a client side operation! Don't
     * execute this on the server side
     *
     * @param title
     *            the title of the toast
     * @param message
     *            the message of the toast
     */
    public static void displayToast(Component title, Component message) {
        Minecraft minecraft = Minecraft.getInstance();

        minecraft.getToasts().addToast(
                SystemToast.multiline(minecraft, SystemToast.SystemToastIds.PERIODIC_NOTIFICATION, title, message));
    }

}
