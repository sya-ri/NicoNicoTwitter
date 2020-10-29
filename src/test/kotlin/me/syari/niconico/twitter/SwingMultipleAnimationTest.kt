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
        private val width: Double,
        private var x: Int,
        private val y: Int,
        private val text: String
    ) {
        fun draw(g: Graphics) {
            g.drawString(text, x, y)
            x --
        }

        class Manager(
            private val marginX: Int,
            private val marginY: Int,
            private val beginY: Int
        ) {
            private val renderList = mutableSetOf<AnimationString>()

            val size
                get() = renderList.size

            fun add(
                panel: AnimationPanel,
                text: String
            ) {
                val width = panel.width
                val bounds = FontDesignMetrics.getMetrics(panel.font).getStringBounds(text, null)
                val notAvailableY = renderList.filter { width < (it.x + it.width) }.map { it.y }
                val y = sequence {
                    var y = beginY
                    while (true) {
                        yield(y)
                        y += bounds.height.toInt() + marginY
                    }
                }.first { notAvailableY.contains(it).not() }
                renderList.add(AnimationString(marginX + bounds.width, width, y, text))
            }

            fun draw(g: Graphics) {
                renderList.forEach { it.draw(g) } // 再描写
                renderList.removeIf { (it.x + it.width) < 0 } // 左端まで行って表示されなくなったら削除
            }
        }
    }

    class AnimationPanel: JPanel() {
        private val animationTimer = Timer(10) {
            repaint()
        }

        private val animationStringManager = AnimationString.Manager(20, 10, 30)

        init {
            font = Font("Arial", Font.PLAIN, 24)
        }

        fun start() {
            animationTimer.start()
        }

        fun addString(text: String) {
            animationStringManager.add(this, text)
        }

        override fun paintComponent(g: Graphics) {
            super.paintComponent(g)

            g.drawString(animationStringManager.size.toString(), 10, 30)
            animationStringManager.draw(g)
        }
    }
}