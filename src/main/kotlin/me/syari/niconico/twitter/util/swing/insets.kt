@file:Suppress("NOTHING_TO_INLINE")

package me.syari.niconico.twitter.util.swing

import java.awt.*

inline fun insets(top: Int, left: Int, bottom: Int, right: Int) = Insets(top, left, bottom, right)
inline fun insets(horizontal: Int, vertical: Int) = insets(horizontal, vertical, horizontal, vertical)
inline fun insets(all: Int) = insets(all, all)
