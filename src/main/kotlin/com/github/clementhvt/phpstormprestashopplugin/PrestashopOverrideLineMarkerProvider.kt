package com.github.clementhvt.phpstormprestashopplugin

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.icons.AllIcons
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.jetbrains.php.lang.psi.PhpFile
import com.jetbrains.php.lang.psi.elements.PhpClass
import javax.swing.Icon

class PrestashopOverrideLineMarkerProvider : RelatedItemLineMarkerProvider(), DumbAware {

    private val overrideIcon: Icon = IconLoader.getIcon("/actions/regexSelected.svg", javaClass)

    override fun collectNavigationMarkers(
        element: PsiElement,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>>
    ) {
        // 1) Ne traiter que l'élément feuille correspondant à l'identifiant du nom de la classe
        val phpClass = element.parent as? PhpClass ?: return

        // s'assurer qu'on est bien sur le nameIdentifier (feuille)
        if (element !== phpClass.nameIdentifier) return

        val className = phpClass.name ?: return
        if (!className.endsWith("Core")) return

        val baseName = className.removeSuffix("Core")
        val project = phpClass.project
        val basePath = project.basePath ?: return

        val overridePath = "$basePath/override/classes/$baseName.php"
        val vf = LocalFileSystem.getInstance().findFileByPath(overridePath) ?: return

        val psiFile = PsiManager.getInstance(project).findFile(vf) ?: return

        val builder = NavigationGutterIconBuilder.create(AllIcons.Hierarchy.Subtypes)
            .setTargets(psiFile)
            .setTooltipText("Aller à l'override : $baseName.php")

        result.add(builder.createLineMarkerInfo(element))
    }

}