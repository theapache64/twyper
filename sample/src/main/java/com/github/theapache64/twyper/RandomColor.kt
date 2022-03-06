package com.github.theapache64.twyper

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import com.github.theapache64.twyper.Generator.randomColor

private val rgbRange = 0..255

/**
 * Generate a random color that's survivable through configuration changes
 */
@Composable
fun rememberRandomColor(): Color {
    return rememberSaveable(
        saver = Saver(
            save = { color -> color.value.toLong() },
            restore = { colorLong -> Color(colorLong.toULong()) }
        )
    ) {
       randomColor()
    }
}

object Generator{
    fun randomColor(): Color {
        return Color(
            red = rgbRange.random(),
            green = rgbRange.random(),
            blue = rgbRange.random(),
        )
    }
}
