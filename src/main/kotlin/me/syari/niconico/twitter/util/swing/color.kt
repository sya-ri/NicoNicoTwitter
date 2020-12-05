package me.syari.niconico.twitter.util.swing

import java.awt.Color

fun String.toColor(): Color? {
    return try {
        Color.decode(this)
    } catch (ex: NumberFormatException) {
        null
    }
}
