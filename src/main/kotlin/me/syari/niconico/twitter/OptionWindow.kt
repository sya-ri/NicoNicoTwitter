package me.syari.niconico.twitter

import blue.starry.penicillin.core.exceptions.PenicillinException
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.syari.niconico.twitter.api.TwitterAPI
import java.awt.Desktop
import java.awt.Rectangle
import javax.swing.*


object OptionWindow {
    fun show() {
        JFrame().apply {
            defaultCloseOperation = JFrame.EXIT_ON_CLOSE // バツボタンの処理
            title = "NicoNicoTwitter" // ウィンドウタイトル
            bounds = Rectangle(450, 600) // ウィンドウサイズを指定
            isResizable = false // サイズ変更を無効化
            setLocationRelativeTo(null) // ウィンドウを中心に配置
            add(JPanel().apply {
                val twitterIdTextField = add(JTextField(16).apply {
                    isEnabled = false
                }) as JTextField
                add(JButton("認証").apply {
                    addActionListener {
                        GlobalScope.launch {
                            isEnabled = false
                            val generateResult = TwitterAPI.AuthURLProvider.generate()
                            withContext(Dispatchers.IO) {
                                Desktop.getDesktop().browse(generateResult.url.toURI())
                            }
                            val pin = JOptionPane("PINコードを入力してください").apply {
                                wantsInput = true // 入力を受けつける
                                createDialog("Twitter 認証").apply {
                                    isAlwaysOnTop = true // ウィンドウを最前面で固定する
                                    isVisible = true // ウィンドウを表示する
                                }
                            }.inputValue
                            isEnabled = true
                            if (pin !is String) return@launch
                            val accessTokenResponse = try {
                                TwitterAPI.AuthURLProvider.enterPin(generateResult, pin)
                            } catch (ex: PenicillinException) {
                                JOptionPane("認証に失敗しました").apply {
                                    createDialog("エラー").apply {
                                        isAlwaysOnTop = true // ウィンドウを最前面で固定する
                                        isVisible = true // ウィンドウを表示する
                                    }
                                }
                                return@launch
                            }
                            twitterIdTextField.text = "@" + accessTokenResponse.screenName
                        }
                    }
                })
            })
            isVisible = true // ウィンドウを表示
        }
    }
}