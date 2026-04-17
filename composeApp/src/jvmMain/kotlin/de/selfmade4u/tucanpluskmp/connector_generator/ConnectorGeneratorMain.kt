package de.selfmade4u.tucanpluskmp.connector_generator

import com.intellij.lang.ASTNode
import com.intellij.openapi.extensions.ExtensionPoint
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.UserDataHolderBase
import com.intellij.pom.PomModel
import com.intellij.pom.PomModelAspect
import com.intellij.pom.PomTransaction
import com.intellij.pom.impl.PomTransactionBase
import com.intellij.pom.tree.TreeAspect
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.PsiWhiteSpaceImpl
import com.intellij.psi.impl.source.tree.TreeCopyHandler
import org.jetbrains.kotlin.cli.jvm.compiler.IdeaStandaloneExecutionSetup
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreApplicationEnvironment
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreApplicationEnvironmentMode
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreProjectEnvironment
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.lexer.KtTokens.WHITE_SPACE
import org.jetbrains.kotlin.parsing.KotlinParserDefinition
import org.jetbrains.kotlin.psi.KtDeclarationWithBody
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.psi.psiUtil.children
import org.jetbrains.kotlin.psi.psiUtil.leaves
import org.jetbrains.kotlin.util.prefixIfNot
import sun.reflect.ReflectionFactory
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

private class FormatPomModel : UserDataHolderBase(), PomModel {
    override fun runTransaction(transaction: PomTransaction) {
        (transaction as PomTransactionBase).run()
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : PomModelAspect> getModelAspect(aspect: Class<T>): T? {
        if (aspect == TreeAspect::class.java) {
            // using approach described in https://git.io/vKQTo due to the magical bytecode of TreeAspect
            // (check constructor signature and compare it to the source)
            // (org.jetbrains.kotlin:kotlin-compiler-embeddable:1.0.3)
            val constructor =
                ReflectionFactory
                    .getReflectionFactory()
                    .newConstructorForSerialization(
                        aspect,
                        Any::class.java.getDeclaredConstructor(*arrayOfNulls<Class<*>>(0)),
                    )
            return constructor.newInstance() as T
        }
        return null
    }
}

/**
 * `true` when [ASTNode] is a whitespace element
 */
public val ASTNode?.isWhiteSpace: Boolean
    get() = this != null && elementType == WHITE_SPACE

/**
 * `true` when [ASTNode] is a whitespace element that contains a newline
 */
public val ASTNode?.isWhiteSpaceWithNewline: Boolean
    get(): Boolean = this != null && isWhiteSpace && textContains('\n')

/**
 * The indentation of [ASTNode] including the newline prefix
 */
public val ASTNode.indent: String
    get(): String = indentInternal().prefixIfNot("\n")

/**
 * The indentation of [ASTNode] excluding the newline prefix
 */
public val ASTNode.indentWithoutNewlinePrefix: String
    get(): String = indentInternal().removePrefix("\n")

/**
 * Get the current indentation of the line containing the [ASTNode]
 */
private fun ASTNode.indentInternal(): String =
    leaves(forward = false)
        .firstOrNull { it.isWhiteSpaceWithNewline }
        ?.text
        ?.substringAfterLast('\n')
        .orEmpty() // Fallback if node is not preceded by any newline character

fun main() {
    project.registerService(PomModel::class.java, FormatPomModel())
    val psiFactory = KtPsiFactory(project, markGenerated = false)
    val test = createKtFile("""
        fun main() {
        
        }
    """.trimIndent(), "Test.kt")
    for (declaration in test.declarations) {
        println(declaration::class)
        if (declaration is KtNamedFunction) {
            declaration.bodyBlockExpression!!.node.addChild(PsiWhiteSpaceImpl(declaration.bodyBlockExpression!!.node.indent+"  "), declaration.bodyBlockExpression!!.node.lastChildNode)
            declaration.bodyBlockExpression!!.node.addChild(psiFactory.createExpression("println(\"hello world\")").node, declaration.bodyBlockExpression!!.node.lastChildNode)
        }
    }
    println(test.text)
}
