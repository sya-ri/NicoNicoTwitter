package me.syari.niconico.twitter

import blue.starry.penicillin.extensions.models.*
import kotlinx.coroutines.*
import me.syari.niconico.twitter.api.*
import me.syari.niconico.twitter.util.swing.*
import sun.font.*
import java.awt.*
import java.awt.event.*
import java.awt.image.*
import javax.swing.*

object CommentWindow {
    private var openWindow: JFrame? = null

    fun show(searchWord: String, option: Option) {
        openWindow?.dispose()
        openWindow = jFrame {
            defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE // バツボタンの処理
            title = "NicoNicoTwitter - Comment" // ウィンドウタイトル
            isUndecorated = true // ウィンドウ上部を非表示に変更
            extendedState = Frame.MAXIMIZED_BOTH // スクリーンサイズを最大に変更
            commentPanel(option) {
                start()
                GlobalScope.launch {
                    TwitterAPI.ContinuousSearch.search(buildString {
                        append(searchWord)
                        if (option.ignoreRT) {
                            append(" -RT")
                        }
                    }) {
                        it.forEach { status ->
                            GlobalScope.launch {
                                addComment(status.text)
                            }
                        }
                    }
                }
            }
            addMouseListener(object: MouseAdapter() {
                override fun mouseClicked(e: MouseEvent) {
                    dispose() // クリックでウィンドウを閉じる
                }
            })
            isVisible = true // ウィンドウを表示
        }
        println("open")
    }

    class Option {
        var ignoreRT = false
        var removeUserName = false
        var removeHashTag = false
        var removeUrl = false
        var displayFps = 60
        var displayDurationSecond = 5
        var maxCommentCount = 15
        var textColor: Color = Color.white
        var backGroundColor: Color = Color.black
    }

    private inline fun Container.commentPanel(option: Option, action: CommentPanel.() -> Unit) = addT(CommentPanel(option).apply(action))

    class CommentPanel(val option: Option): JPanel() {
        val commentFont = Font("Arial", Font.PLAIN, 24)

        private val commentManager = Comment.Manager(20, 10, 100)

        fun start() {
            GlobalScope.launch {
                val increaseTime = 1000.0 / option.displayFps
                var nextTime = System.currentTimeMillis() + increaseTime
                while (true) {
                    try {
                        delay(nextTime.toLong() - System.currentTimeMillis())
                        repaint()
                        nextTime += increaseTime
                    } catch (ex: InterruptedException) {
                        ex.printStackTrace()
                    }
                }
            }
        }

        private inline val removeUserNameRegex
            get() = "@[A-Za-z0-9_]+".toRegex()

        private inline val removeHashTagRegex
            get() = "#(w*[一-龠_ぁ-んァ-ヴーａ-ｚＡ-Ｚa-zA-Z0-9]+|[a-zA-Z0-9_]+|[a-zA-Z0-9_]w*)".toRegex()

        private inline val removeUrlRegex
            get() = "https?://[a-zA-Z0-9/:%#&~=_!'$?().+*\\-]+".toRegex()

        suspend fun addComment(text: String) {
            fun String.removedIf(condition: Boolean, regex: Regex) = if (condition) replace(regex, "") else this

            commentManager.add(
                this,
                text.removedIf(option.removeUserName, removeUserNameRegex)
                    .removedIf(option.removeHashTag, removeHashTagRegex)
                    .removedIf(option.removeUrl, removeUrlRegex)
                    .replace("\\s+".toRegex(), " ")
            )
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
            g.drawImage(BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB).apply {
                createGraphics().apply {
                    font = commentFont
                    color = option.textColor
                    background = option.backGroundColor
                    clearRect(0, 0, width, height)
                    setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON) // アンチエイリアスの有効
                    drawString(frameRate.frameRate.toString() + "FPS / " + commentManager.size.toString(), 10, 30)
                    commentManager.draw(this)
                    dispose()
                }
            }, 0, 0, this)
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

            suspend fun add(
                panel: CommentPanel,
                text: String
            ) {
                delay((0 until 5000L).random())
                val bounds = FontDesignMetrics.getMetrics(panel.commentFont).getStringBounds(text, null)
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
                commentList.toList().forEach { it.draw(g) } // 再描写
                commentList.removeIf { (it.x + it.width) < 0 } // 左端まで行って表示されなくなったら削除
            }
        }
    }
}