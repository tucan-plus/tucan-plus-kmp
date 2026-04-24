package de.selfmade4u

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.findPsiFile
import com.intellij.psi.xml.XmlFile

internal class FolderAction : DumbAwareAction() {
    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
    }

    override fun actionPerformed(e: AnActionEvent) {
        val directory = e.getData(CommonDataKeys.VIRTUAL_FILE)!!
        magicFunction(directory, e.project!!)
    }
}

fun magicFunction(
    directory: VirtualFile,
    project: Project
) {
    val files = directory.children
    Notification("Bagel", "Bagel was eaten ${files.contentToString()}", NotificationType.INFORMATION)
        .notify(project)
    for (file in files) {
        val psi = file.findPsiFile(project) as XmlFile
        val tag = psi.rootTag!!
        println("tag $tag")
        val subtags = tag.subTags
        for (subtag in subtags) {
            println("subtag $subtag")
        }

        Notification("Bagel", "psi $psi", NotificationType.INFORMATION)
            .notify(project)
    }
}