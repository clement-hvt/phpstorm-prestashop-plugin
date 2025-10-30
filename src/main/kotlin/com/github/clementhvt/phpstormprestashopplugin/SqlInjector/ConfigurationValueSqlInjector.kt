package com.github.clementhvt.phpstormprestashopplugin.SqlInjector

import com.intellij.lang.injection.MultiHostInjector
import com.intellij.lang.injection.MultiHostRegistrar
import com.intellij.psi.ElementManipulators
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiLanguageInjectionHost
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.sql.psi.SqlLanguage
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression

class ConfigurationValueSqlInjector : MultiHostInjector {
    override fun getLanguagesToInject(registrar: MultiHostRegistrar, host: PsiElement) {
        if (host !is StringLiteralExpression) return

        val methodRef = PsiTreeUtil.getParentOfType(host, MethodReference::class.java) ?: return

        val methodName = methodRef.name ?: return
        if (methodName != "get") return

        val classRef = methodRef.classReference
        val qualifierText = classRef?.text ?: methodRef.text // fallback
        // Accept if qualifier contains "Configuration" (rough but practical)
        if (qualifierText == null || !qualifierText.contains("Configuration")) return
        val params = methodRef.parameterList ?: return
        val firstParam = params.parameters.firstOrNull() ?: return
        if (firstParam !== host) return
        val prefix = "select * from ps_configuration where name=\'"
        val suffix = "\'"

        val text = host.text
        if (text.length < 2) return

        val rangeInsideHost = ElementManipulators.getValueTextRange(host)
        if (rangeInsideHost.isEmpty) return

        registrar.startInjecting(SqlLanguage.INSTANCE)
            .addPlace(prefix, suffix, host as PsiLanguageInjectionHost, rangeInsideHost)
            .doneInjecting()
    }

    override fun elementsToInjectIn(): List<Class<out PsiElement>> {
        return listOf(StringLiteralExpression::class.java)
    }
}