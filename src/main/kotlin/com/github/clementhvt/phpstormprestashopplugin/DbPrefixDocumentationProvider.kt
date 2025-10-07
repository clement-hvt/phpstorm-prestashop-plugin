package com.github.clementhvt.phpstormprestashopplugin

import com.intellij.psi.PsiElement
import com.jetbrains.php.lang.documentation.PhpDocumentationProvider
import com.jetbrains.php.lang.psi.elements.ConstantReference
import org.jetbrains.annotations.Nls

class DbPrefixDocumentationProvider : PhpDocumentationProvider() {
    override fun generateDoc(element: PsiElement, originalElement: PsiElement?): @Nls String? {
        if (element is ConstantReference && element.name == "_DB_PREFIX_") {
            return "T'aimes les patates ?"
        }
        return super.generateDoc(element, originalElement)
    }

    override fun generateHoverDoc(element: PsiElement, originalElement: PsiElement?): @Nls String? {
        if (element is ConstantReference && element.name == "_DB_PREFIX_") {
            return "PrestaShop DB Prefix"
        }
        return super.generateHoverDoc(element, originalElement)
    }
}
