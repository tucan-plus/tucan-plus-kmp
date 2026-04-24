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
    var tags = files.map { (it.findPsiFile(project) as XmlFile).rootTag!! }
    while (true) {
        if (tags.all { it.name == tags[0].name }) {
            tags = tags.map { it.subTags.first() }
        } else {

        }
    }
}