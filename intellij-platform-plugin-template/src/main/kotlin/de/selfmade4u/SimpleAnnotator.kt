package de.selfmade4u

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement

class SimpleAnnotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        //println("annotate")
        ProjectConfiguration().loadProjectConfiguration(element.project, element, holder)
    }
}