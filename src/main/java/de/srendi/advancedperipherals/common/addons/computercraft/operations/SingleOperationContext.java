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
package de.srendi.advancedperipherals.common.addons.computercraft.operations;

import java.io.Serializable;

public class SingleOperationContext implements Serializable {
    private final int distance;
    private int count;

    public SingleOperationContext(int count, int distance) {
        this.count = count;
        this.distance = distance;
    }

    public int getCount() {
        return count;
    }

    public int getDistance() {
        return distance;
    }

    public SingleOperationContext extraCount(int extra) {
        this.count += extra;
        return new SingleOperationContext(extra, distance);
    }
}
