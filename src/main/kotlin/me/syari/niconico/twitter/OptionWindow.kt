package me.syari.niconico.twitter

import java.awt.Rectangle
import javax.swing.JFrame

object OptionWindow {
    fun show() {
        JFrame().apply {
            defaultCloseOperation = JFrame.EXIT_ON_CLOSE // バツボタンの処理
            title = "NicoNicoTwitter" // ウィンドウタイトル
            bounds = Rectangle(450, 600) // ウィンドウサイズを指定
            isResizable = false // サイズ変更を無効化
            setLocationRelativeTo(null) // ウィンドウを中心に配置
            isVisible = true // ウィンドウを表示
        }
    }
}