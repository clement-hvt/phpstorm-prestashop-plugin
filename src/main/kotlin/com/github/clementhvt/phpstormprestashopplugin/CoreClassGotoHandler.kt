package com.github.clementhvt.phpstormprestashopplugin

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.ClassReference

class CoreClassGotoHandler: GotoDeclarationHandler {
    override fun getGotoDeclarationTargets(
        element: PsiElement?,
        off: Int,
        editor: Editor?
    ): Array<out PsiElement?>? {
        if (element?.parent !is ClassReference || !element.isValid) {
            return null
        }

        val project = element.project
        val className = element.text ?: return null
        val targetClass = PhpIndex.getInstance(project)
            .getClassesByName(className + "Core")
            .firstOrNull()

        return if (targetClass != null) arrayOf(targetClass) else null
    }

    override fun getActionText(context: DataContext): String = "Go to Core Class"
}