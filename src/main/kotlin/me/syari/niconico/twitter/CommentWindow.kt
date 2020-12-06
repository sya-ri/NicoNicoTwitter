package me.syari.niconico.twitter

import blue.starry.penicillin.extensions.models.*
import kotlinx.coroutines.*
import me.syari.niconico.twitter.api.*
import me.syari.niconico.twitter.util.swing.*
import sun.font.*
import java.awt.*
import java.awt.event.*
import java.awt.geom.*
import java.awt.image.*
import java.io.*
import java.net.*
import javax.imageio.*
import javax.swing.*
import kotlin.math.*

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
                    TwitterAPI.ContinuousSearch.search(
                        buildString {
                            append(searchWord)
                            if (option.ignoreRT) {
                                append(" -RT")
                            }
                        }
                    ) {
                        it.forEach { status ->
                            GlobalScope.launch {
                                val icon = withContext(Dispatchers.IO) {
                                    try {
                                        ImageIO.read(URL(status.user.profileImageUrlHttps))
                                    } catch (ex: IOException) {
                                        null
                                    }
                                }
                                addComment(icon, status.text)
                            }
                        }
                    }
                }
            }
            addMouseListener(
                object : MouseAdapter() {
                    override fun mouseClicked(e: MouseEvent) {
                        dispose() // クリックでウィンドウを閉じる
                    }
                }
            )
            isVisible = true // ウィンドウを表示
        }
    }

    class Option(
        val ignoreRT: Boolean,
        val removeUserName: Boolean,
        val removeHashTag: Boolean,
        val removeUrl: Boolean,
        val displayFps: Int,
        val displayDurationSecond: Int,
        val maxCommentCount: Int,
        val textColor: Color,
        val backGroundColor: Color,
        val highlightWord: String?,
        val highlightColor: Color,
        val marginX: Int,
        val marginY: Int,
        val beginY: Int,
        val commentSize: Int,
        val iconSize: Int
    )

    private inline fun Container.commentPanel(option: Option, action: CommentPanel.() -> Unit) = addT(CommentPanel(option).apply(action))

    class CommentPanel(val option: Option) : JPanel() {
        val commentFont = Font(Font.SANS_SERIF, Font.BOLD, option.commentSize)

        val commentManager = Comment.Manager(option.marginX, option.marginY, option.beginY)

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

        suspend fun addComment(icon: BufferedImage?, text: String) {
            fun String.removedIf(condition: Boolean, regex: Regex) = if (condition) replace(regex, "") else this

            val highlightWord = option.highlightWord
            val isHighlight = highlightWord?.let { it.isNotBlank() && text.contains(it) } ?: false
            val color = if (isHighlight) option.highlightColor else option.textColor
            commentManager.add(
                this,
                icon,
                text.removedIf(option.removeUserName, removeUserNameRegex)
                    .removedIf(option.removeHashTag, removeHashTagRegex)
                    .removedIf(option.removeUrl, removeUrlRegex)
                    .replace("\\s+".toRegex(), " "),
                color
            )
        }

        override fun paintComponent(g: Graphics) {
            super.paintComponent(g)

            g.drawImage(
                bufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB) {
                    createGraphics {
                        font = commentFont
                        background = option.backGroundColor
                        clearRect(0, 0, width, height)
                        setRenderingHint(
                            RenderingHints.KEY_TEXT_ANTIALIASING,
                            RenderingHints.VALUE_TEXT_ANTIALIAS_ON
                        ) // アンチエイリアスの有効
                        commentManager.draw(this)
                        dispose()
                    }
                },
                0,
                0,
                this
            )
        }
    }

    class Comment(
        private val panel: CommentPanel,
        private val commentBounds: Rectangle2D,
        private var x: Int,
        private val y: Int,
        private val icon: BufferedImage?,
        private val text: String,
        private val color: Color,
        private val speedX: Int
    ) {
        companion object {
            const val DistanceIconAndComment = 5
        }

        private val iconSize = panel.option.iconSize
        private val width = panel.commentManager.marginX + commentBounds.width + iconSize

        fun draw(g: Graphics) {
            g.color = color
            if (icon != null) {
                val scaleImg = BufferedImage(iconSize, iconSize, BufferedImage.TYPE_3BYTE_BGR)
                scaleImg.createGraphics().drawImage(
                    icon.getScaledInstance(iconSize, iconSize, Image.SCALE_AREA_AVERAGING),
                    0,
                    0,
                    iconSize,
                    iconSize,
                    null
                )
                g.drawImage(icon, x, y, panel.option.iconSize, panel.option.iconSize, panel)
            }
            g.drawString(text, x + iconSize + DistanceIconAndComment, y + ((iconSize - commentBounds.y) / 2).toInt())
            x -= speedX
        }

        class Manager(
            val marginX: Int,
            private val marginY: Int,
            private val beginY: Int
        ) {
            private val commentList = mutableSetOf<Comment>()

            val size
                get() = commentList.size

            suspend fun add(
                panel: CommentPanel,
                icon: BufferedImage?,
                text: String,
                color: Color
            ) {
                delay((0 until TwitterAPI.ContinuousSearch.IntervalMillis).random())
                val bounds = FontDesignMetrics.getMetrics(panel.commentFont).getStringBounds(text, null)
                val notAvailableY = synchronized(commentList) {
                    commentList.filter { panel.width < (it.x + it.width) }.map { it.y }
                }
                val boundsY = max(bounds.height.toInt(), panel.option.iconSize)
                val y = sequence {
                    var y = beginY
                    while (true) {
                        yield(y)
                        y += boundsY + marginY
                    }
                }.first { notAvailableY.contains(it).not() }
                if (panel.height < y || panel.option.maxCommentCount <= size) return
                val speedX = (panel.width + bounds.width) / panel.option.displayDurationSecond / panel.option.displayFps
                synchronized(commentList) {
                    commentList.add(
                        Comment(
                            panel,
                            bounds,
                            panel.width,
                            y,
                            icon,
                            text,
                            color,
                            speedX.toInt()
                        )
                    )
                }
            }

            fun draw(g: Graphics) {
                synchronized(commentList) {
                    commentList.forEach { it.draw(g) } // 再描写
                    commentList.removeIf { (it.x + it.width) < 0 } // 左端まで行って表示されなくなったら削除
                }
            }
        }
    }
}
