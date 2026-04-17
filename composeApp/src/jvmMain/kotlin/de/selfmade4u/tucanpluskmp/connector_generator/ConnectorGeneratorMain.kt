package de.selfmade4u.tucanpluskmp.connector_generator

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.Disposer
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.cli.jvm.compiler.IdeaStandaloneExecutionSetup
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreApplicationEnvironment
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreApplicationEnvironmentMode
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreProjectEnvironment
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.parsing.KotlinParserDefinition
import org.jetbrains.kotlin.psi.KtDeclarationWithBody
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtPsiFactory
import kotlin.getValue

val project by lazy {
    val disposable = Disposer.newDisposable()
    IdeaStandaloneExecutionSetup.doSetup()
    val applicationEnvironment = KotlinCoreApplicationEnvironment.create(
        disposable,
        KotlinCoreApplicationEnvironmentMode.Production,
    ).also {
        it.registerFileType(KotlinFileType.INSTANCE, "kt")
        it.registerFileType(KotlinFileType.INSTANCE, KotlinParserDefinition.STD_SCRIPT_SUFFIX)
        it.registerParserDefinition(KotlinParserDefinition())
    }
    KotlinCoreProjectEnvironment(disposable, applicationEnvironment).project
}

fun createKtFile(codeString: String, fileName: String) =
    KtPsiFactory(project, markGenerated = false).createFile(fileName, codeString)

fun main() {
    val test = createKtFile("""
        fun main() {
        
        }
    """.trimIndent(), "Test.kt")
    for (declaration in test.declarations) {
        println(declaration::class)
        if (declaration is KtNamedFunction) {
            println(declaration.bodyBlockExpression!!.add())
        }
    }
}
