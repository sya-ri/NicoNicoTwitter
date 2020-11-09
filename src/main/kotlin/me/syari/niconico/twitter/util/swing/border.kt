@file:Suppress("NOTHING_TO_INLINE")

package me.syari.niconico.twitter.util.swing

import javax.swing.border.*

inline fun emptyBorder(top: Int, left: Int, bottom: Int, right: Int) = EmptyBorder(top, left, bottom, right)
inline fun emptyBorder(horizontal: Int, vertical: Int) = emptyBorder(horizontal, vertical, horizontal, vertical)
inline fun emptyBorder(all: Int) = emptyBorder(all, all)