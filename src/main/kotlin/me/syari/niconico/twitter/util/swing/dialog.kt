package me.syari.niconico.twitter.util.swing

import javax.swing.*

inline fun jOptionPane(message: String, action: JOptionPane.() -> Unit) = JOptionPane(message).apply(action)
inline fun dialog(title: String, message: String, optionPaneAction: JOptionPane.() -> Unit, dialogAction: JDialog.() -> Unit) = jOptionPane(message) {
    optionPaneAction()
    createDialog(title).apply(dialogAction)
}
inline fun messageDialog(title: String, message: String, action: JDialog.() -> Unit) = dialog(title, message, {}, action)
inline fun inputDialog(title: String, message: String, action: JDialog.() -> Unit) = dialog(title, message, { wantsInput = true }, action)
