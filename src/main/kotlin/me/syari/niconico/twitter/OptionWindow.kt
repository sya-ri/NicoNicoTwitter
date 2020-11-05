package me.syari.niconico.twitter

import blue.starry.penicillin.core.exceptions.*
import io.ktor.http.*
import kotlinx.coroutines.*
import me.syari.niconico.twitter.api.*
import me.syari.niconico.twitter.util.swing.*
import java.awt.*
import javax.swing.*
import javax.swing.border.*


object OptionWindow {
    fun show() {
        jFrame {
            defaultCloseOperation = JFrame.EXIT_ON_CLOSE // バツボタンの処理
            title = "NicoNicoTwitter" // ウィンドウタイトル
            bounds = Rectangle(450, 250) // ウィンドウサイズを指定
            isResizable = false // サイズ変更を無効化
            setLocationRelativeTo(null) // ウィンドウを中心に配置
            jPanel {
                val gridBagLayout = GridBagLayout()

                // y: 0
                jLabel("Twitter") {
                    gridBagLayout.setConstraints(this, gridBagConstraints {
                        gridy = 0
                        gridx = 0
                    })
                }
                val twitterIdTextField = jTextField {
                    gridBagLayout.setConstraints(this, gridBagConstraints {
                        gridy = 0
                        gridx = 1
                        gridwidth = 4
                        fill = GridBagConstraints.HORIZONTAL
                    })
                    isEnabled = false
                }
                jButton("認証") {
                    gridBagLayout.setConstraints(this, gridBagConstraints {
                        gridy = 0
                        gridx = 5
                    })
                    addActionListener {
                        actionTwitterAuth(twitterIdTextField)
                    }
                }

                // y: 1
                jLabel("検索ワード") {
                    gridBagLayout.setConstraints(this, gridBagConstraints {
                        gridy = 1
                        gridx = 0
                    })
                }
                val twitterSearchWord = jTextField {
                    gridBagLayout.setConstraints(this, gridBagConstraints {
                        gridy = 1
                        gridx = 1
                        gridwidth = 5
                        fill = GridBagConstraints.HORIZONTAL
                    })
                }

                // y: 2
                jLabel("除外") {
                    gridBagLayout.setConstraints(this, gridBagConstraints {
                        gridy = 2
                        gridx = 0
                    })
                }
                val ignoreRTCheckBox = jCheckBox("RT") {
                    gridBagLayout.setConstraints(this, gridBagConstraints {
                        gridy = 2
                        gridx = 1
                    })
                }
                val removeUserNameCheckbox = jCheckBox("Username") {
                    gridBagLayout.setConstraints(this, gridBagConstraints {
                        gridy = 2
                        gridx = 2
                    })
                }
                val removeHashTagCheckBox = jCheckBox("Hashtag") {
                    gridBagLayout.setConstraints(this, gridBagConstraints {
                        gridy = 2
                        gridx = 3
                    })
                }
                val removeUrlCheckBox = jCheckBox("URL") {
                    gridBagLayout.setConstraints(this, gridBagConstraints {
                        gridy = 2
                        gridx = 4
                    })
                }

                // y: 3
                jLabel("FPS") {
                    gridBagLayout.setConstraints(this, gridBagConstraints {
                        gridy = 3
                        gridx = 0
                    })
                }
                val fpsTextField = jFormatTextField(integerFormat {
                    allowsInvalid = false
                    minimum = 0
                    maximum = 60
                }) {
                    gridBagLayout.setConstraints(this, gridBagConstraints {
                        gridy = 3
                        gridx = 1
                        fill = GridBagConstraints.HORIZONTAL
                    })
                }
                jLabel("表示時間") {
                    gridBagLayout.setConstraints(this, gridBagConstraints {
                        gridy = 3
                        gridx = 2
                    })
                }
                val durationTextField = jFormatTextField(integerFormat {
                    allowsInvalid = false
                    minimum = 0
                    maximum = 30
                }) {
                    gridBagLayout.setConstraints(this, gridBagConstraints {
                        gridy = 3
                        gridx = 3
                        fill = GridBagConstraints.HORIZONTAL
                    })
                }
                jLabel("最大表示数") {
                    gridBagLayout.setConstraints(this, gridBagConstraints {
                        gridy = 3
                        gridx = 4
                    })
                }
                val maxCommentTextField = jFormatTextField(integerFormat {
                    allowsInvalid = false
                    minimum = 0
                    maximum = 500
                }) {
                    gridBagLayout.setConstraints(this, gridBagConstraints {
                        gridy = 3
                        gridx = 5
                        fill = GridBagConstraints.HORIZONTAL
                    })
                }

                // y: 4
                jButton("実行") {
                    gridBagLayout.setConstraints(this, gridBagConstraints {
                        gridy = 4
                        gridx = 3
                    })
                    addActionListener {
                        CommentWindow.show(twitterSearchWord.text, CommentWindow.Option().apply {
                            ignoreRT = ignoreRTCheckBox.isSelected
                            removeUserName = removeUserNameCheckbox.isSelected
                            removeHashTag = removeHashTagCheckBox.isSelected
                            removeUrl = removeUrlCheckBox.isSelected
                            displayFps = fpsTextField.value as Int
                            displayDurationSecond = durationTextField.value as Int
                            maxCommentCount = maxCommentTextField.value as Int
                        })
                    }
                }
                layout = gridBagLayout
                border = EmptyBorder(10, 10, 10, 10)
            }
            isVisible = true // ウィンドウを表示
        }
    }

    private fun JButton.actionTwitterAuth(twitterIdTextField: JTextField) {
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