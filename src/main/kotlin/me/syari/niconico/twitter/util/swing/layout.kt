package me.syari.niconico.twitter.util.swing

import java.awt.*

inline fun gridBagConstraints(action: GridBagConstraints.() -> Unit) = GridBagConstraints().apply(action)
