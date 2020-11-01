package me.syari.niconico.twitter.util.swing

import java.awt.*
import javax.swing.*

inline fun jFrame(action: JFrame.() -> Unit) = JFrame().apply(action)
inline fun <reified T: Component> Container.addT(component: T) = add(component) as T
inline fun Container.jPanel(action: JPanel.() -> Unit) = addT(JPanel().apply(action))
inline fun Container.jLabel(text: String, action: JLabel.() -> Unit) = addT(JLabel(text).apply(action))
inline fun Container.jTextField(action: JTextField.() -> Unit) = addT(JTextField().apply(action))
inline fun Container.jButton(text: String, action: JButton.() -> Unit) = addT(JButton(text).apply(action))
inline fun Container.jCheckBox(text: String, action: JCheckBox.() -> Unit) = addT(JCheckBox(text).apply(action))