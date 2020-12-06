package me.syari.niconico.twitter

import blue.starry.penicillin.core.exceptions.*
import io.ktor.http.*
import kotlinx.coroutines.*
import me.syari.niconico.twitter.api.*
import me.syari.niconico.twitter.util.swing.*
import java.awt.*
import javax.swing.*

object OptionWindow {
    fun show() {
        jFrame {
            defaultCloseOperation = JFrame.EXIT_ON_CLOSE // バツボタンの処理
            title = "NicoNicoTwitter" // ウィンドウタイトル
            isResizable = false // サイズ変更を無効化
            jPanel {
                val gridBagLayout = GridBagLayout()

                // y: -1
                for (i in 0..5) {
                    add(
                        Box.createHorizontalStrut(90).apply {
                            gridBagLayout.setConstraints(
                                this,
                                gridBagConstraints {
                                    gridy = -1
                                    gridx = i
                                }
                            )
                        }
                    )
                }

                // y: 0
                jLabel("Twitter") {
                    gridBagLayout.setConstraints(
                        this,
                        gridBagConstraints {
                            gridy = 0
                            gridx = 0
                            insets = insets(3)
                        }
                    )
                }
                val twitterIdTextField = jTextField {
                    gridBagLayout.setConstraints(
                        this,
                        gridBagConstraints {
                            gridy = 0
                            gridx = 1
                            gridwidth = 4
                            fill = GridBagConstraints.HORIZONTAL
                            insets = insets(3)
                        }
                    )
                    isEnabled = false
                }
                jButton("認証") {
                    gridBagLayout.setConstraints(
                        this,
                        gridBagConstraints {
                            gridy = 0
                            gridx = 5
                            insets = insets(3)
                        }
                    )
                    addActionListener {
                        actionTwitterAuth(twitterIdTextField)
                    }
                }

                // y: 1
                jLabel("検索ワード") {
                    gridBagLayout.setConstraints(
                        this,
                        gridBagConstraints {
                            gridy = 1
                            gridx = 0
                            insets = insets(3)
                        }
                    )
                }
                val twitterSearchWord = jTextField {
                    gridBagLayout.setConstraints(
                        this,
                        gridBagConstraints {
                            gridy = 1
                            gridx = 1
                            gridwidth = 2
                            fill = GridBagConstraints.HORIZONTAL
                            insets = insets(3)
                        }
                    )
                }
                jLabel("強調ワード") {
                    gridBagLayout.setConstraints(
                        this,
                        gridBagConstraints {
                            gridy = 1
                            gridx = 3
                            insets = insets(3)
                        }
                    )
                }
                val highlightWord = jTextField {
                    gridBagLayout.setConstraints(
                        this,
                        gridBagConstraints {
                            gridy = 1
                            gridx = 4
                            gridwidth = 2
                            fill = GridBagConstraints.HORIZONTAL
                            insets = insets(3)
                        }
                    )
                }

                // y: 2
                jLabel("除外") {
                    gridBagLayout.setConstraints(
                        this,
                        gridBagConstraints {
                            gridy = 2
                            gridx = 0
                            insets = insets(3)
                        }
                    )
                }
                val ignoreRTCheckBox = jCheckBox("リツイート") {
                    alignmentX = 0.0F
                    gridBagLayout.setConstraints(
                        this,
                        gridBagConstraints {
                            gridy = 2
                            gridx = 1
                            fill = GridBagConstraints.HORIZONTAL
                            insets = insets(3)
                        }
                    )
                }
                val removeUserNameCheckbox = jCheckBox("ユーザー名") {
                    alignmentX = 0.0F
                    gridBagLayout.setConstraints(
                        this,
                        gridBagConstraints {
                            gridy = 2
                            gridx = 2
                            fill = GridBagConstraints.HORIZONTAL
                            insets = insets(3)
                        }
                    )
                }
                val removeHashTagCheckBox = jCheckBox("ハッシュタグ") {
                    alignmentX = 0.0F
                    gridBagLayout.setConstraints(
                        this,
                        gridBagConstraints {
                            gridy = 2
                            gridx = 3
                            fill = GridBagConstraints.HORIZONTAL
                            insets = insets(3)
                        }
                    )
                }
                val removeUrlCheckBox = jCheckBox("URL") {
                    alignmentX = 0.0F
                    gridBagLayout.setConstraints(
                        this,
                        gridBagConstraints {
                            gridy = 2
                            gridx = 4
                            fill = GridBagConstraints.HORIZONTAL
                            insets = insets(3)
                        }
                    )
                }

                // y: 3
                jLabel("FPS") {
                    gridBagLayout.setConstraints(
                        this,
                        gridBagConstraints {
                            gridy = 3
                            gridx = 0
                            insets = insets(3)
                        }
                    )
                }
                val fpsTextField = jFormatTextField(
                    integerFormat {
                        allowsInvalid = false
                        minimum = 0
                        maximum = 60
                    }
                ) {
                    value = 60
                    gridBagLayout.setConstraints(
                        this,
                        gridBagConstraints {
                            gridy = 3
                            gridx = 1
                            fill = GridBagConstraints.HORIZONTAL
                            insets = insets(3)
                        }
                    )
                }
                jLabel("表示時間") {
                    gridBagLayout.setConstraints(
                        this,
                        gridBagConstraints {
                            gridy = 3
                            gridx = 2
                            insets = insets(3)
                        }
                    )
                }
                val durationTextField = jFormatTextField(
                    integerFormat {
                        allowsInvalid = false
                        minimum = 0
                        maximum = 30
                    }
                ) {
                    value = 5
                    gridBagLayout.setConstraints(
                        this,
                        gridBagConstraints {
                            gridy = 3
                            gridx = 3
                            fill = GridBagConstraints.HORIZONTAL
                            insets = insets(3)
                        }
                    )
                }
                jLabel("最大表示数") {
                    gridBagLayout.setConstraints(
                        this,
                        gridBagConstraints {
                            gridy = 3
                            gridx = 4
                            insets = insets(3)
                        }
                    )
                }
                val maxCommentTextField = jFormatTextField(
                    integerFormat {
                        allowsInvalid = false
                        minimum = 0
                        maximum = 500
                    }
                ) {
                    value = 100
                    gridBagLayout.setConstraints(
                        this,
                        gridBagConstraints {
                            gridy = 3
                            gridx = 5
                            fill = GridBagConstraints.HORIZONTAL
                            insets = insets(3)
                        }
                    )
                }

                // y: 4
                jLabel("背景色") {
                    gridBagLayout.setConstraints(
                        this,
                        gridBagConstraints {
                            gridy = 4
                            gridx = 0
                            insets = insets(3)
                        }
                    )
                }
                val backgroundColor = jTextField {
                    text = "#000000"
                    gridBagLayout.setConstraints(
                        this,
                        gridBagConstraints {
                            gridy = 4
                            gridx = 1
                            fill = GridBagConstraints.HORIZONTAL
                            insets = insets(3)
                        }
                    )
                }
                jLabel("文字色") {
                    gridBagLayout.setConstraints(
                        this,
                        gridBagConstraints {
                            gridy = 4
                            gridx = 2
                            insets = insets(3)
                        }
                    )
                }
                val textColor = jTextField {
                    text = "#FFFFFF"
                    gridBagLayout.setConstraints(
                        this,
                        gridBagConstraints {
                            gridy = 4
                            gridx = 3
                            fill = GridBagConstraints.HORIZONTAL
                            insets = insets(3)
                        }
                    )
                }
                jLabel("強調色") {
                    gridBagLayout.setConstraints(
                        this,
                        gridBagConstraints {
                            gridy = 4
                            gridx = 4
                            insets = insets(3)
                        }
                    )
                }
                val highlightColor = jTextField {
                    text = "#FFFF00"
                    gridBagLayout.setConstraints(
                        this,
                        gridBagConstraints {
                            gridy = 4
                            gridx = 5
                            fill = GridBagConstraints.HORIZONTAL
                            insets = insets(3)
                        }
                    )
                }

                // y: 5
                jLabel("コメント余白 X") {
                    gridBagLayout.setConstraints(
                        this,
                        gridBagConstraints {
                            gridy = 5
                            gridx = 0
                            insets = insets(3)
                        }
                    )
                }
                val marginX = jFormatTextField(
                    integerFormat {
                        allowsInvalid = false
                    }
                ) {
                    value = 75
                    gridBagLayout.setConstraints(
                        this,
                        gridBagConstraints {
                            gridy = 5
                            gridx = 1
                            fill = GridBagConstraints.HORIZONTAL
                            insets = insets(3)
                        }
                    )
                }
                jLabel("コメント余白 Y") {
                    gridBagLayout.setConstraints(
                        this,
                        gridBagConstraints {
                            gridy = 5
                            gridx = 2
                            insets = insets(3)
                        }
                    )
                }
                val marginY = jFormatTextField(
                    integerFormat {
                        allowsInvalid = false
                    }
                ) {
                    value = 25
                    gridBagLayout.setConstraints(
                        this,
                        gridBagConstraints {
                            gridy = 5
                            gridx = 3
                            fill = GridBagConstraints.HORIZONTAL
                            insets = insets(3)
                        }
                    )
                }
                jLabel("コメント開始 Y") {
                    gridBagLayout.setConstraints(
                        this,
                        gridBagConstraints {
                            gridy = 5
                            gridx = 4
                            insets = insets(3)
                        }
                    )
                }
                val beginY = jFormatTextField(
                    integerFormat {
                        allowsInvalid = false
                    }
                ) {
                    value = 50
                    gridBagLayout.setConstraints(
                        this,
                        gridBagConstraints {
                            gridy = 5
                            gridx = 5
                            fill = GridBagConstraints.HORIZONTAL
                            insets = insets(3)
                        }
                    )
                }

                // y: 6
                jLabel("文字サイズ") {
                    gridBagLayout.setConstraints(
                        this,
                        gridBagConstraints {
                            gridy = 6
                            gridx = 0
                            insets = insets(3)
                        }
                    )
                }
                val commentSize = jFormatTextField(
                    integerFormat {
                        allowsInvalid = false
                    }
                ) {
                    value = 36
                    gridBagLayout.setConstraints(
                        this,
                        gridBagConstraints {
                            gridy = 6
                            gridx = 1
                            fill = GridBagConstraints.HORIZONTAL
                            insets = insets(3)
                        }
                    )
                }
                jLabel("アイコンサイズ") {
                    gridBagLayout.setConstraints(
                        this,
                        gridBagConstraints {
                            gridy = 6
                            gridx = 2
                            insets = insets(3)
                        }
                    )
                }
                val iconSize = jFormatTextField(
                    integerFormat {
                        allowsInvalid = false
                    }
                ) {
                    value = 48
                    gridBagLayout.setConstraints(
                        this,
                        gridBagConstraints {
                            gridy = 6
                            gridx = 3
                            fill = GridBagConstraints.HORIZONTAL
                            insets = insets(3)
                        }
                    )
                }

                // y: 7
                jButton("実行") {
                    gridBagLayout.setConstraints(
                        this,
                        gridBagConstraints {
                            gridy = 7
                            gridx = 2
                            gridwidth = 2
                            insets = insets(3)
                        }
                    )
                    addActionListener {
                        if (twitterIdTextField.text.isNullOrEmpty()) {
                            messageDialog("エラー", "ツイッター認証されていません") {
                                isVisible = true
                            }
                            return@addActionListener
                        }
                        if (twitterSearchWord.text.isNullOrEmpty()) {
                            messageDialog("エラー", "検索ワードが入力されていません") {
                                isVisible = true
                            }
                            return@addActionListener
                        }
                        val textColorValue = textColor.text.toColor() ?: return@addActionListener run {
                            messageDialog("エラー", "文字色が不正です") {
                                isVisible = true
                            }
                        }
                        val backgroundColorValue = backgroundColor.text.toColor() ?: return@addActionListener run {
                            messageDialog("エラー", "背景色が不正です") {
                                isVisible = true
                            }
                        }
                        val highlightColorValue = highlightColor.text.toColor() ?: return@addActionListener run {
                            messageDialog("エラー", "強調色が不正です") {
                                isVisible = true
                            }
                        }
                        CommentWindow.show(
                            twitterSearchWord.text,
                            CommentWindow.Option(
                                ignoreRTCheckBox.isSelected,
                                removeUserNameCheckbox.isSelected,
                                removeHashTagCheckBox.isSelected,
                                removeUrlCheckBox.isSelected,
                                fpsTextField.value.toString().toInt(),
                                durationTextField.value.toString().toInt(),
                                maxCommentTextField.value.toString().toInt(),
                                textColorValue,
                                backgroundColorValue,
                                highlightWord.text,
                                highlightColorValue,
                                marginX.value.toString().toInt(),
                                marginY.value.toString().toInt(),
                                beginY.value.toString().toInt(),
                                commentSize.value.toString().toInt(),
                                iconSize.value.toString().toInt()
                            )
                        )
                    }
                }
                border = emptyBorder(10)
                layout = gridBagLayout
            }
            pack() // ウィンドウサイズを整える
            setLocationRelativeTo(null) // ウィンドウを中心に配置
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
                messageDialog("エラー", "URLの発行に失敗しました") {
                    isAlwaysOnTop = true // ウィンドウを最前面で固定する
                    isVisible = true // ウィンドウを表示する
                }
                return@launch
            }

            // 発行したURLを開く
            withContext(Dispatchers.IO) {
                Desktop.getDesktop().browse(generateResult.url.toURI())
            }

            // PINを入力
            val pin = inputDialog("Twitter 認証", "PINコードを入力してください") {
                isAlwaysOnTop = true // ウィンドウを最前面で固定する
                isVisible = true // ウィンドウを表示する
            }.inputValue

            // 多重ウィンドウの防止
            isEnabled = true

            // 入力したピンで認証
            if (pin !is String) return@launch
            val accessTokenResponse = try {
                TwitterAPI.AuthURLProvider.enterPin(generateResult, pin)
            } catch (ex: PenicillinException) {
                messageDialog("エラー", "認証に失敗しました") {
                    isAlwaysOnTop = true // ウィンドウを最前面で固定する
                    isVisible = true // ウィンドウを表示する
                }
                return@launch
            }

            // 認証成功時に TextField の文字列を変更
            twitterIdTextField.text = "@" + accessTokenResponse.screenName
        }
    }
}
