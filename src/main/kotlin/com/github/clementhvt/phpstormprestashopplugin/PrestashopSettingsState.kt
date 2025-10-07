
package com.github.clementhvt.phpstormprestashopplugin

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@State(
    name = "com.github.clementhvt.phpstormprestashopplugin.DbPrefixSettingsState",
    storages = [Storage("prestashop-db-prefix-settings.xml")]
)
class PrestashopSettingsState : PersistentStateComponent<PrestashopSettingsState.State> {

    class State {
        var dbPrefix: String = DEFAULT_PREFIX
    }

    private var myState = State()

    override fun getState(): State = myState

    override fun loadState(p0: State) {
        myState = state
    }

    companion object {
        const val DEFAULT_PREFIX = "ps_"

        fun getInstance(): PrestashopSettingsState {
            return ApplicationManager.getApplication().getService(PrestashopSettingsState::class.java)
        }
    }
}