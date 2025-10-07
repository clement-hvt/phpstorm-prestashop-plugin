package com.github.clementhvt.phpstormprestashopplugin

import com.intellij.openapi.options.Configurable
import javax.swing.*

class PrestashopConfigurable : Configurable {
    private var prestashopSettingsComponent: PrestashopSettingsComponent? = null

    override fun getDisplayName(): String = "PrestaShop DB Prefix"


    override fun createComponent(): JComponent? {
        prestashopSettingsComponent = PrestashopSettingsComponent()
        return prestashopSettingsComponent?.getPanel()
    }

    override fun isModified(): Boolean {
        val currentValue = prestashopSettingsComponent?.getDbPrefixName()
        val savedValue = PrestashopSettingsState.getInstance().state.dbPrefix
        return currentValue != savedValue
    }

    override fun apply() {
        val newValue = prestashopSettingsComponent?.getDbPrefixName() ?: PrestashopSettingsState.DEFAULT_PREFIX
        PrestashopSettingsState.getInstance().state.dbPrefix = newValue.ifEmpty { "ps_" }
    }

    override fun getPreferredFocusedComponent(): JComponent? {
        return prestashopSettingsComponent?.getPreferredFocusedComponent()
    }

    override fun reset() {
        val previousValue = PrestashopSettingsState.getInstance().state.dbPrefix
        prestashopSettingsComponent?.setDbPrefixName(previousValue)
    }

    override fun disposeUIResources() {
        prestashopSettingsComponent = null
    }
}