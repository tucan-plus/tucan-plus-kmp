package de.selfmade4u

import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.idea.base.util.projectScope
import org.jetbrains.kotlin.idea.stubindex.KotlinAnnotationsIndex
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtStringTemplateExpression
import org.jetbrains.kotlin.psi.psiUtil.getParentOfType

// https://github.com/JetBrains/intellij-community/blob/37b233a6b70fb606dad5f13eb001c63fb6b12cf4/plugins/kotlin/base/analysis/src/org/jetbrains/kotlin/idea/stubindex/IdeStubIndexService.java#L306
// https://github.com/JetBrains/kotlin/blob/a66400f13ad48df4ed889ba98d94c0ece05d9acf/compiler/psi/psi-impl/src/org/jetbrains/kotlin/psi/stubs/elements/StubIndexService.kt#L12

// my first task should be to extract the information from the kotlin html parsing code.
// then we can try to generate autofixes in all directions

class ProjectConfiguration {

    fun loadProjectConfiguration(project: Project) {
        val keys = KotlinAnnotationsIndex.getAllKeys(project)
        val annotations = KotlinAnnotationsIndex["HtmlFromResources", project, project.projectScope()];
        println("annotations $annotations")
        for (annotationEntry in annotations) {
            val valueArg = annotationEntry.valueArgumentList!!.arguments.first()
            val text = valueArg.getArgumentExpression() as KtStringTemplateExpression
            println("path ${text.entries.first().text}")
            val ktNamedFunction = annotationEntry.getParentOfType<KtNamedFunction>(strict = true)!!
            println("abc ${ktNamedFunction.text}")
            val block = ktNamedFunction.bodyBlockExpression!!
            for (statement in block.statements) {
                println("statement ${statement.text}")
            }
        }
    }
}