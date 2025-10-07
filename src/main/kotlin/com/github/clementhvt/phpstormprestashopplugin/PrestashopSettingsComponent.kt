package com.github.clementhvt.phpstormprestashopplugin

import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent
import javax.swing.JPanel

class PrestashopSettingsComponent {
    private val prestashopSettings: JPanel
    private val dbPrefixName = JBTextField()

    init {
        prestashopSettings = FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("DB Prefix :"), dbPrefixName, 1, false)
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }

    fun getPanel(): JPanel = prestashopSettings

    fun getPreferredFocusedComponent(): JComponent = dbPrefixName

    fun getDbPrefixName(): String = dbPrefixName.text

    fun setDbPrefixName(newText: String) {
        dbPrefixName.text = newText
    }
}