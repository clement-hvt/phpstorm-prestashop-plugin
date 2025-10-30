
package com.github.clementhvt.phpstormprestashopplugin

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project
import com.intellij.psi.util.parentOfType
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.Field
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.elements.impl.ClassConstantReferenceImpl

@State(
    name = "com.github.clementhvt.phpstormprestashopplugin.DbPrefixSettingsState",
    storages = [Storage("prestashop-db-prefix-settings.xml")]
)
class PrestashopSettingsState : PersistentStateComponent<PrestashopSettingsState.State> {

    class State {
        var dbPrefix: String = DEFAULT_PREFIX
        var prestashopVersion: Version? = null
    }

    private var prestashopSettingsState = State()

    override fun getState(): State = prestashopSettingsState

    override fun loadState(p0: State) {
        prestashopSettingsState = p0
    }

    companion object {
        const val DEFAULT_PREFIX = "ps_"

        fun getInstance(): PrestashopSettingsState {
            return ApplicationManager.getApplication().getService(PrestashopSettingsState::class.java)
        }
    }

    data class Version(val major: Int, val minor: Int, val patch: Int) {
        companion object {
            fun fromString(version: String): Version {
                val parts = version.split(".")
                return Version(
                    parts.getOrNull(0)?.toIntOrNull() ?: 0,
                    parts.getOrNull(1)?.toIntOrNull() ?: 0,
                    parts.getOrNull(2)?.toIntOrNull() ?: 0
                )
            }
        }
    }

    fun updatePrestashopVersion(project: Project): Version? {
        val appKernel = PhpIndex.getInstance(project).getClassesByFQN("\\AppKernel").firstOrNull() ?: return null

        return resolveVersionFromClass(appKernel, mutableSetOf())
    }

    private fun resolveVersionFromClass(phpClass: PhpClass, visited: MutableSet<PhpClass>): Version? {
        if (visited.contains(phpClass)) return null
        visited.add(phpClass)

        val versionConst: Field? = phpClass.fields.firstOrNull { it.name == "VERSION" }
        versionConst?.let { field ->
            val defaultValue = field.defaultValue
            if (defaultValue is ClassConstantReferenceImpl) {
                val reference = defaultValue.reference?.resolve()?.parentOfType<PhpClass>()
                if (reference !== null) {
                    return resolveVersionFromClass(reference, visited)
                }
                return null
            }

            if (defaultValue != null) {
                val text = defaultValue.text.trim('\'', '"')
                return Version.fromString(text)
            }
        }

        return null
    }
}