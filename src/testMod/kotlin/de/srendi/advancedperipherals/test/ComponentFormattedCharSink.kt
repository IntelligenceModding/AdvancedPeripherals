package de.srendi.advancedperipherals.test

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.util.FormattedCharSink

class ComponentFormattedCharSink : FormattedCharSink {
    var currentStyle: Style? = null;
    private var currentText: String = "";
    private val components = mutableListOf<Component>();

    override fun accept(pPositionInCurrentSequence: Int, pStyle: Style, pCodePoint: Int): Boolean {
        if (currentStyle?.equals(pStyle) == false) {
            if (currentText.isNotEmpty()) {
                components.add(Component.literal(currentText).withStyle(simplifyStyle(currentStyle!!)))
                currentText = ""
            }
        }

        currentStyle = pStyle
        currentText += String(Character.toChars(pCodePoint))

        return true
    }

    fun getComponents(): List<Component>{
        if (currentText.isNotEmpty()) {
            components.add(Component.literal(currentText).withStyle(simplifyStyle(currentStyle!!)))
            currentText = ""
        }

        return components
    }

    private fun simplifyStyle(style: Style): Style {
        return style
            .withBold(if (style.isBold) true else null)
            .withItalic(if (style.isItalic) true else null)
            .withUnderlined(if (style.isUnderlined) true else null)
            .withStrikethrough(if (style.isStrikethrough) true else null)
            .withObfuscated(if (style.isObfuscated) true else null)
    }

}