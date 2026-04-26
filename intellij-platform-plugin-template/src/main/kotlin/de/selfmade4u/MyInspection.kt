package de.selfmade4u

import com.intellij.codeInspection.HintAction
import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.psi.PsiFile

class MyInspection : LocalInspectionTool() {

    override fun checkFile(
        file: PsiFile,
        manager: InspectionManager,
        isOnTheFly: Boolean
    ): Array<out ProblemDescriptor?> {
       return arrayOf(manager.createProblemDescriptor(file.firstChild, "Test inspection problem", LocalQuickFix.from(MyQuickFix(file)), ProblemHighlightType.ERROR,
           isOnTheFly))
    }
}