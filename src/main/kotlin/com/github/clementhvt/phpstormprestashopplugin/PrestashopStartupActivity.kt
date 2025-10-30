package com.github.clementhvt.phpstormprestashopplugin

import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity

class PrestashopStartupActivity : ProjectActivity {
    override suspend fun execute(project: Project) {
        val settings = PrestashopSettingsState.getInstance()

        settings.state.prestashopVersion = settings.updatePrestashopVersion(project)
    }
}