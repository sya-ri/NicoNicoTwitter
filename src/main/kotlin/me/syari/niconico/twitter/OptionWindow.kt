package me.syari.niconico.twitter

import blue.starry.penicillin.core.exceptions.*
import io.ktor.http.*
import kotlinx.coroutines.*
import me.syari.niconico.twitter.api.*
import java.awt.*
import javax.swing.*
import javax.swing.border.*

object OptionWindow {
    fun show() {
        JFrame().apply {
            defaultCloseOperation = JFrame.EXIT_ON_CLOSE // バツボタンの処理
            title = "NicoNicoTwitter" // ウィンドウタイトル
            bounds = Rectangle(450, 150) // ウィンドウサイズを指定
            isResizable = false // サイズ変更を無効化
            setLocationRelativeTo(null) // ウィンドウを中心に配置
            add(JPanel().apply {
                val gridBagLayout = GridBagLayout()
                add(JLabel("Twitter").apply {
                    gridBagLayout.setConstraints(this, GridBagConstraints().apply {
                        gridx = 0
                        gridy = 0
                    })
                })
                val twitterIdTextField = add(JTextField().apply {
                    gridBagLayout.setConstraints(this, GridBagConstraints().apply {
                        weightx = 1.0
                        gridy = 0
                        gridwidth = 4
                        fill = GridBagConstraints.HORIZONTAL
                    })
                    isEnabled = false
                }) as JTextField
                add(TwitterAuthButton(twitterIdTextField).apply {
                    gridBagLayout.setConstraints(this, GridBagConstraints().apply {
                        gridx = 5
                        gridy = 0
                    })
                })
                add(JLabel("検索ワード").apply {
                    gridBagLayout.setConstraints(this, GridBagConstraints().apply {
                        gridx = 0
                        gridy = 1
                    })
                })
                val twitterSearchWord = add(JTextField().apply {
                    gridBagLayout.setConstraints(this, GridBagConstraints().apply {
                        weightx = 1.0
                        gridy = 1
                        gridwidth = 5
                        fill = GridBagConstraints.HORIZONTAL
                    })
                }) as JTextField
                add(JLabel("除外").apply {
                    gridBagLayout.setConstraints(this, GridBagConstraints().apply {
                        gridx = 0
                        gridy = 2
                    })
                })
                val ignoreRTCheckBox = add(JCheckBox("RT").apply {
                    gridBagLayout.setConstraints(this, GridBagConstraints().apply {
                        gridx = 1
                        gridy = 2
                    })
                }) as JCheckBox
                val removeHashTagCheckBox = add(JCheckBox("Hashtag").apply {
                    gridBagLayout.setConstraints(this, GridBagConstraints().apply {
                        gridx = 2
                        gridy = 2
                    })
                }) as JCheckBox
                val removeUrlCheckBox = add(JCheckBox("URL").apply {
                    gridBagLayout.setConstraints(this, GridBagConstraints().apply {
                        gridx = 3
                        gridy = 2
                    })
                }) as JCheckBox
                add(JButton("実行").apply {
                    gridBagLayout.setConstraints(this, GridBagConstraints().apply {
                        gridx = 3
                        gridy = 3
                    })
                    addActionListener {
                        CommentWindow.show(twitterSearchWord.text, CommentWindow.Option().apply {
                            ignoreRT = ignoreRTCheckBox.isSelected
                            removeHashTag = removeHashTagCheckBox.isSelected
                            removeUrl = removeUrlCheckBox.isSelected
                        })
                    }
                })
                layout = gridBagLayout
                border = EmptyBorder(10, 10, 10, 10)
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