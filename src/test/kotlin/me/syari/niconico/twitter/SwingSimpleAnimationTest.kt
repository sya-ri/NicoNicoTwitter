package me.syari.niconico.twitter

import sun.font.*
import java.awt.*
import javax.swing.*

object SwingSimpleAnimationTest {
    @JvmStatic
    fun main(args: Array<String>) {
        JFrame().apply {
            defaultCloseOperation = JFrame.EXIT_ON_CLOSE // バツボタンの処理
            title = "SwingSimpleAnimationTest" // ウィンドウタイトル
            bounds = Rectangle(900, 600) // ウィンドウサイズを指定
            setLocationRelativeTo(null) // ウィンドウを中心に配置
            add(
                AnimationPanel().apply {
                    start()
                }
            )
            isVisible = true // ウィンドウを表示
        }
    }

    class AnimationPanel: JPanel() {
        private val animationTimer = Timer(10) {
            repaint()
        }

        fun start() {
            animationTimer.start()
        }

        private var helloX = 900

        override fun paintComponent(g: Graphics) {
            super.paintComponent(g)

            val fontMetrics = FontDesignMetrics.getMetrics(g.font)
            val needBounds = fontMetrics.getStringBounds("Hello", null)
            val isVisible = 0 < (helloX + needBounds.width)
            if (isVisible) {
                g.drawString("Hello", helloX, 100)
                helloX --
            } else {
                helloX = 900
            }
        }
    }
}
