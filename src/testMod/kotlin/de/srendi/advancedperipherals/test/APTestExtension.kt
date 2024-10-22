package de.srendi.advancedperipherals.test

import net.minecraft.gametest.framework.GameTestHelper

fun isInRange(value: Double, start: Double, end: Double): Boolean {
    val EPSILON = 1e-7

    return value >= start - EPSILON && value < end + EPSILON
}

fun GameTestHelper.assertDoubleInRange(value: Double, start: Double, end: Double, message: String) {
    if (!(isInRange(value, start, end))) {
        fail("$message, is $value, should be between $start and $end")
    }
}

fun GameTestHelper.assertDoubleIs(value: Double, compare: Double, message: String) {
    if (!(isInRange(value, compare, compare))) {
        fail("$message, is $value, should be $compare")
    }
}

fun GameTestHelper.assertDoubleIsNot(value: Double, compare: Double, message: String) {
    if ((isInRange(value, compare, compare))) {
        fail("$message, is $value, should be $compare")
    }
}

