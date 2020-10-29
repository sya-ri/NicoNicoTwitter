package me.syari.niconico.twitter

import sun.font.FontDesignMetrics
import java.awt.Font
import java.awt.Graphics
import java.awt.Rectangle
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.Timer

object SwingMultipleAnimationTest {
    @JvmStatic
    fun main(args: Array<String>) {
        val animationPanel: AnimationPanel
        JFrame().apply {
            defaultCloseOperation = JFrame.EXIT_ON_CLOSE // バツボタンの処理
            title = "SwingAdvancedAnimationTest" // ウィンドウタイトル
            bounds = Rectangle(900, 600) // ウィンドウサイズを指定
            setLocationRelativeTo(null) // ウィンドウを中心に配置
            animationPanel = add(AnimationPanel().apply {
                start()
            }) as AnimationPanel
            isVisible = true // ウィンドウを表示
        }
        while (true) {
            val text = readLine() ?: continue
            animationPanel.addString(text)
        }
    }

    class AnimationString(
        private var x: Int,
        private val y: Int,
        private val text: String,
        font: Font
    ) {
        private val width = FontDesignMetrics.getMetrics(font).getStringBounds(text, null).width

        private inline val isNotVisible
            get() = (x + width) < 0

        fun draw(g: Graphics) {
            g.drawString(text, x, y)
            x --
        }

        class Manager {
            private val renderList = mutableSetOf<AnimationString>()

            val size
                get() = renderList.size

            fun add(
                x: Int,
                y: Int,
                text: String,
                font: Font
            ) {
                renderList.add(AnimationString(x, y, text, font))
            }

            fun draw(g: Graphics) {
                renderList.forEach {
                    it.draw(g)
                }
                renderList.removeIf {
                    it.isNotVisible
                }
            }
        }
    }

    class AnimationPanel: JPanel() {
        private val animationTimer = Timer(10) {
            repaint()
        }

        private val animationStringManager = AnimationString.Manager()

        init {
            font = Font("Arial", Font.PLAIN, 24)
        }

        fun start() {
            animationTimer.start()
        }

        fun addString(text: String) {
            animationStringManager.add(width, 100, text, font)
        }

        override fun paintComponent(g: Graphics) {
            super.paintComponent(g)

            g.drawString(animationStringManager.size.toString(), 10, 30)
            animationStringManager.draw(g)
        }
    }
}