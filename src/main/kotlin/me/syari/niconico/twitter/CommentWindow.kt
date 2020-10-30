package me.syari.niconico.twitter

import blue.starry.penicillin.extensions.models.text
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.syari.niconico.twitter.api.TwitterAPI
import sun.font.FontDesignMetrics
import java.awt.Font
import java.awt.Frame
import java.awt.Graphics
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.Timer

object CommentWindow {
    private var openWindow: JFrame? = null

    fun show(searchWord: String, option: Option) {
        openWindow?.dispose()
        openWindow = JFrame().apply {
            defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE // バツボタンの処理
            title = "NicoNicoTwitter - Comment" // ウィンドウタイトル
            isUndecorated = true // ウィンドウ上部を非表示に変更
            extendedState = Frame.MAXIMIZED_BOTH // スクリーンサイズを最大に変更
            add(CommentPanel(option).apply {
                start()
                GlobalScope.launch {
                    TwitterAPI.ContinuousSearch.search(buildString {
                        append(searchWord)
                        if (option.ignoreRT) {
                            append(" -RT")
                        }
                    }) {
                        it.forEach { status ->
                            addComment(status.text.run {
                                if (option.removeHashTag) {
                                    replace("#(w*[一-龠_ぁ-んァ-ヴーａ-ｚＡ-Ｚa-zA-Z0-9]+|[a-zA-Z0-9_]+|[a-zA-Z0-9_]w*)".toRegex(), "")
                                } else {
                                    this
                                }
                            })
                        }
                    }
                }
            })
            isVisible = true // ウィンドウを表示
        }
        println("open")
    }

    class Option {
        var ignoreRT = false
        var removeHashTag = false
        var displayFps = 60
        var displayDurationSecond = 5
        var maxCommentCount = 15
    }

    class CommentPanel(val option: Option): JPanel() {
        private val animationTimer = Timer(1000 / option.displayFps) {
            repaint()
        }

        private val commentManager = Comment.Manager(20, 10, 30)

        init {
            font = Font("Arial", Font.PLAIN, 24)
        }

        fun start() {
            animationTimer.start()
        }

        fun addComment(text: String) {
            commentManager.add(this, text)
        }

        private val frameRate = FrameRate(500)
        class FrameRate(private val updateTimeMillis: Int) {
            private var lastTime = System.currentTimeMillis()
            private var count = 0

            var frameRate = 0f
                private set

            fun process(): Boolean {
                val currentTime = System.currentTimeMillis()
                count ++
                return if (updateTimeMillis <= currentTime - lastTime) {
                    frameRate = (count * 1000).toFloat() / (currentTime - lastTime).toFloat()
                    lastTime = currentTime
                    count = 0
                    true
                } else {
                    false
                }
            }
        }

        override fun paintComponent(g: Graphics) {
            super.paintComponent(g)

            frameRate.process()
            g.drawString(frameRate.frameRate.toString() + "FPS / " + commentManager.size.toString(), 10, 30)
            commentManager.draw(g)
        }
    }

    class Comment(
        private val width: Double,
        private var x: Int,
        private val y: Int,
        private val text: String,
        private val speedX: Int
    ) {
        fun draw(g: Graphics) {
            g.drawString(text, x, y)
            x -= speedX
        }

        class Manager(
            private val marginX: Int,
            private val marginY: Int,
            private val beginY: Int
        ) {
            private val commentList = mutableSetOf<Comment>()

            val size
                get() = commentList.size

            fun add(
                panel: CommentPanel,
                text: String
            ) {
                val bounds = FontDesignMetrics.getMetrics(panel.font).getStringBounds(text, null)
                val notAvailableY = commentList.filter { panel.width < (it.x + it.width) }.map { it.y }
                val y = sequence {
                    var y = beginY
                    while (true) {
                        yield(y)
                        y += bounds.height.toInt() + marginY
                    }
                }.first { notAvailableY.contains(it).not() }
                if (panel.height < y || panel.option.maxCommentCount <= size) return
                val speedX = (panel.width + bounds.width) / panel.option.displayDurationSecond / panel.option.displayFps
                commentList.add(Comment(marginX + bounds.width, panel.width, y, text, speedX.toInt()))
            }

            fun draw(g: Graphics) {
                commentList.forEach { it.draw(g) } // 再描写
                commentList.removeIf { (it.x + it.width) < 0 } // 左端まで行って表示されなくなったら削除
            }
        }
    }
}