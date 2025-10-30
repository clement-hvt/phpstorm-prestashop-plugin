package com.github.clementhvt.phpstormprestashopplugin

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.icons.AllIcons
import com.intellij.openapi.project.DumbAware
import com.intellij.psi.PsiElement
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.PhpClass

class PrestashopCQRSHandlerLineMarkerProvider  : RelatedItemLineMarkerProvider(), DumbAware {
    override fun collectNavigationMarkers(
        element: PsiElement,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>>
    ) {
        val phpClass = element.parent as? PhpClass ?: return
        if (element !== phpClass.nameIdentifier) return

        if (!isCqrsClass(phpClass)) return

        val project = phpClass.project
        val index = PhpIndex.getInstance(project)

        val handlers = findHandlersForType(index, phpClass.fqn)
        if (handlers.isEmpty()) return

        val builder = NavigationGutterIconBuilder
            .create(AllIcons.Nodes.Plugin)
            .setTargets(handlers)
            .setTooltipText("Aller vers le Handler associé")

        result.add(builder.createLineMarkerInfo(element))
    }

    private fun isCqrsClass(phpClass: PhpClass): Boolean {
        val name = phpClass.namespaceName
        return name.contains("Command") || name.contains("Query")
    }

    private fun findHandlersForType(index: PhpIndex, classFqn: String): List<PhpClass> {
        val result = mutableListOf<PhpClass>()

        for (name in index.getAllClassFqns(null)) {
            if (!name.contains("Handler")) {
                continue
            }
            val classes = index.getClassesByFQN(name)
            for (cls in classes) {
                val handleMethod = cls.findMethodByName("handle") ?: continue
                val firstParam = handleMethod.parameters.firstOrNull() ?: continue
                val types = firstParam.declaredType.types
                if (classFqn in types) {
                    result.add(cls)
                }
            }
        }

        return result
    }
}