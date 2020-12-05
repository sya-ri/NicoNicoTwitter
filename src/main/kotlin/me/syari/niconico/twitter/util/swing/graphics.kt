package me.syari.niconico.twitter.util.swing

import java.awt.*
import java.awt.image.*

fun bufferedImage(width: Int, height: Int, imageType: Int, action: BufferedImage.() -> Unit) = BufferedImage(width, height, imageType).apply(action)

fun BufferedImage.createGraphics(action: Graphics2D.() -> Unit): Graphics2D = createGraphics().apply(action)
