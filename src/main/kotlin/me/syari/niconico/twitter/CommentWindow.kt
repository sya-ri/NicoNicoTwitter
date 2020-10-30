package me.syari.niconico.twitter

import java.awt.Frame
import javax.swing.JFrame

object CommentWindow {
    private var openWindow: JFrame? = null

    fun show() {
        openWindow?.dispose()
        openWindow = JFrame().apply {
            defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE // バツボタンの処理
            title = "NicoNicoTwitter - Comment" // ウィンドウタイトル
            isUndecorated = true // ウィンドウ上部を非表示に変更
            extendedState = Frame.MAXIMIZED_BOTH // スクリーンサイズを最大に変更
            isVisible = true // ウィンドウを表示
        }
        println("open")
    }
}