package me.syari.niconico.twitter.util.swing

import java.awt.*
import java.awt.image.*

fun bufferedImage(width: Int, height: Int, imageType: Int, action: BufferedImage.() -> Unit) = BufferedImage(width, height, imageType).apply(action)

fun BufferedImage.createGraphics(action: Graphics2D.() -> Unit): Graphics2D = createGraphics().apply(action)

fun Graphics.drawEdgeString(text: String, x: Int, y: Int, textColor: Color, edgeColor: Color) {
    val correctY = y + fontMetrics.ascent
    val lastColor = color
    color = edgeColor
    drawString(text, x - 1, correctY)
    drawString(text, x - 1, correctY - 1)
    drawString(text, x, correctY - 1)
    drawString(text, x + 1, correctY - 1)
    drawString(text, x + 1, correctY)
    drawString(text, x + 1, correctY + 1)
    drawString(text, x, correctY + 1)
    drawString(text, x - 1, correctY + 1)
    color = textColor
    drawString(text, x, correctY)
    color = lastColor
}
