package com.github.clementhvt.phpstormprestashopplugin.SqlInjector

import com.github.clementhvt.phpstormprestashopplugin.PrestashopSettingsState
import com.intellij.lang.injection.MultiHostInjector
import com.intellij.lang.injection.MultiHostRegistrar
import com.intellij.patterns.PatternCondition
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.ElementManipulators
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiLanguageInjectionHost
import com.intellij.sql.psi.SqlLanguage
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression
import com.jetbrains.php.lang.psi.elements.ArrayHashElement
import com.jetbrains.php.lang.psi.elements.Field
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression

class ObjectModelTableDefinitionSqlInjector : MultiHostInjector {
    val TABLE_VALUE_PATTERN = PlatformPatterns.psiElement(PsiElement::class.java)
        .withSuperParent(
            2,
            PlatformPatterns.psiElement(ArrayHashElement::class.java)
                .with(object : PatternCondition<ArrayHashElement>("keyIsTable") {
                    override fun accepts(arrayElement: ArrayHashElement, context: ProcessingContext?): Boolean {
                        val key = arrayElement.key as? StringLiteralExpression ?: return false
                        return key.contents == "table"
                    }
                })
                .withParent(
                    PlatformPatterns.psiElement(ArrayCreationExpression::class.java)
                        .withParent(
                            PlatformPatterns.psiElement(Field::class.java)
                                .withName("definition")
                        )
                )
        )

    override fun elementsToInjectIn(): List<Class<out PsiElement>> {
        return listOf(StringLiteralExpression::class.java)
    }

    override fun getLanguagesToInject(registrar: MultiHostRegistrar, host: PsiElement) {
        if (host !is StringLiteralExpression) {
            return
        }
        if (!TABLE_VALUE_PATTERN.accepts(host)) return


        val rawTableName = host.contents
        if (rawTableName.isBlank() || rawTableName.equals("table")) return

        val sqlCode = "SELECT * FROM " + PrestashopSettingsState.getInstance().state.dbPrefix
        val rangeInsideHost = ElementManipulators.getValueTextRange(host)
        if (rangeInsideHost.isEmpty) return

        registrar.startInjecting(SqlLanguage.INSTANCE)
            .addPlace(
                sqlCode,
                "",
                host as PsiLanguageInjectionHost,
                rangeInsideHost
            ).doneInjecting()
    }
}