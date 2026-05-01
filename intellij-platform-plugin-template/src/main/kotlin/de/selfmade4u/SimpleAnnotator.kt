package de.selfmade4u

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.psi.PsiElement

class SimpleAnnotator : Annotator {
    // called for every single element so idk why it works with this holder stuff
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        Extractor().process(element.project, element, holder)
    }
}