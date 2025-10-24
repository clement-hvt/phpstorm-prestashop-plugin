package com.github.clementhvt.phpstormprestashopplugin.TypeProvider

import com.github.clementhvt.phpstormprestashopplugin.PrestashopSettingsState
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.jetbrains.php.lang.psi.elements.ConstantReference
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider4

class DbPrefixConstanteTypeProvider: PhpTypeProvider4 {
    override fun getKey(): Char {
        return '马'
    }

    override fun getType(element: PsiElement?): PhpType? {
        if (element is ConstantReference && element.name == "_DB_PREFIX_") {
            val value = PrestashopSettingsState.getInstance().state.dbPrefix
            return PhpType().add("#${getKey()}$value")
        }
        return null
    }

    override fun complete(
        expression: String?,
        project: Project?
    ): PhpType? {
        if (expression == null || !expression.startsWith("#${getKey()}")) {
            return null
        }

        // Décode la signature pour extraire la valeur du prefix
        val value = expression.substring(2) // Enlève "#马"

        // Retourne un type string avec la valeur littérale
        return PhpType().add(PhpType.STRING).add("\"$value\"")
    }

    override fun getBySignature(
        p0: String?,
        p1: Set<String?>?,
        p2: Int,
        p3: Project?
    ): Collection<PhpNamedElement?>? {
        return null
    }
}