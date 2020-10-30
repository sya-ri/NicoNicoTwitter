package me.syari.niconico.twitter

import blue.starry.penicillin.core.exceptions.PenicillinException
import io.ktor.http.toURI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.syari.niconico.twitter.api.TwitterAPI
import java.awt.Desktop
import java.awt.Rectangle
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.JTextField

object OptionWindow {
    fun show() {
        JFrame().apply {
            defaultCloseOperation = JFrame.EXIT_ON_CLOSE // バツボタンの処理
            title = "NicoNicoTwitter" // ウィンドウタイトル
            bounds = Rectangle(450, 600) // ウィンドウサイズを指定
            isResizable = false // サイズ変更を無効化
            setLocationRelativeTo(null) // ウィンドウを中心に配置
            add(JPanel().apply {
                add(JLabel("Twitter"))
                val twitterIdTextField = add(JTextField(16).apply {
                    isEnabled = false
                }) as JTextField
                add(TwitterAuthButton(twitterIdTextField))
                add(JButton("実行").apply {
                    addActionListener {
                        CommentWindow.show()
                    }
                })
            })
            isVisible = true // ウィンドウを表示
        }
    }

    class TwitterAuthButton(twitterIdTextField: JTextField): JButton() {
        init {
            text = "認証"
            addActionListener {
                GlobalScope.launch {
                    // 多重ウィンドウの防止
                    isEnabled = false

                    // 認証URLを発行
                    val generateResult = try {
                        TwitterAPI.AuthURLProvider.generate()
                    } catch (ex: PenicillinException) {
                        JOptionPane("URLの発行に失敗しました").apply {
                            createDialog("エラー").apply {
                                isAlwaysOnTop = true // ウィンドウを最前面で固定する
                                isVisible = true // ウィンドウを表示する
                            }
                        }
                        return@launch
                    }

                    // 発行したURLを開く
                    withContext(Dispatchers.IO) {
                        Desktop.getDesktop().browse(generateResult.url.toURI())
                    }

                    // PINを入力
                    val pin = JOptionPane("PINコードを入力してください").apply {
                        wantsInput = true // 入力を受けつける
                        createDialog("Twitter 認証").apply {
                            isAlwaysOnTop = true // ウィンドウを最前面で固定する
                            isVisible = true // ウィンドウを表示する
                        }
                    }.inputValue

                    // 多重ウィンドウの防止
                    isEnabled = true

                    // 入力したピンで認証
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

                    // 認証成功時に TextField の文字列を変更
                    twitterIdTextField.text = "@" + accessTokenResponse.screenName
                }
            }
        }
    }
}