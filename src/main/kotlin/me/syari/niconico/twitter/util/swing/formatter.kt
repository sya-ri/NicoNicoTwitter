package me.syari.niconico.twitter.util.swing

import java.text.*
import javax.swing.text.*

inline fun integerFormat(action: NumberFormatter.() -> Unit) = NumberFormatter(NumberFormat.getIntegerInstance()).apply(action)