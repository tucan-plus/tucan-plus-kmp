package de.selfmade4u

import java.util.PriorityQueue

object Extractor2 {

    class ParseAttribute() {

    }

    sealed class ParsingInstruction {
        class ParseText(val text: Regex) : ParsingInstruction()
        class ParseElement(val name: String, val attributes: List<ParseAttribute>, val children: List<ParseElement>): ParsingInstruction()
        // Additional error types can be added here
    }

    fun myFun(
        project: Project,
        annotationEntry: KtAnnotationEntry
    ): CachedValueProvider.Result<MutableMap<PsiElement, AnnotationResult>> {
        val annotations = mutableMapOf<PsiElement, AnnotationResult>()
        val valueArg = annotationEntry.valueArgumentList!!.arguments.first()
        val path = getStringLiteral(valueArg.stringTemplateExpression!!)
        thisLogger().warn("project dir ${project.guessProjectDir()} path $path");
        thisLogger().warn("htmls ${project.guessProjectDir()!!.findDirectory(path)}")
        val htmls = project.guessProjectDir()!!.findDirectory(path)!! // TODO FIXME error handling

        val ktNamedFunction = annotationEntry.getParentOfType<KtNamedFunction>(strict = true)!!
        //println("abc ${ktNamedFunction.text}")
        val block = ktNamedFunction.bodyBlockExpression!!

        val files = htmls.children
        thisLogger().warn("htmls2 ${files.map { (it.findPsiFile(project) as XmlFile).rootTag }}")
        val htmlFiles = files.map { (it.findPsiFile(project) as XmlFile).rootTag!! }

        // do work here
        // for now assume that we always create the parsers from scratch and that the input html files don't change. this should make it much simpler

        val workToDo = PriorityQueue()
        workToDo.add(ParsingInstruction.ParseElement("html", listOf(), listOf()))

        while (!workToDo.isEmpty()) {
            val first = workToDo.first();

            // try adding a new element:
            // for efficiency the states should store where in the html they currently are and where in the html parsing state they currently are
            // that means both of these should be stored in an efficient way, because otherwise this traversal here will be very slow.
            // currently both could be linear right? which would make this much simpler? though at least for parsing a tree would help
        }

        return CachedValueProvider.Result(annotations, annotationEntry, htmls)
    }

    fun process(project: Project, annotationContext: PsiElement?, holder: AnnotationHolder?) {
        val annotations = KotlinAnnotationsIndex["HtmlFromResources", project, project.projectScope()];
        //println("annotations $annotations")
        for (annotationEntry in annotations) { // oh does the loop fail if one of them fails?
            // https://github.com/JetBrains/intellij-community/blob/master/platform/core-api/src/com/intellij/psi/util/CachedValue.java

            //ApplicationManager.getApplication().logError()

            try {
                val annotations =
                    CachedValuesManager.getManager(project).getCachedValue(annotationEntry) {
                        myFun(
                            project,
                            annotationEntry
                        )
                    }
                if (annotationContext != null && holder != null) {
                    annotations[annotationContext]?.let { info ->
                        holder.newAnnotation(HighlightSeverity.ERROR, info.message)
                            .range(annotationContext)
                            .apply { info.quickFix?.let { withFix(it) } }
                            .create()
                    }
                }
            } catch (error: Throwable) {
                LOG.error(error)
            }
        }
    }
}