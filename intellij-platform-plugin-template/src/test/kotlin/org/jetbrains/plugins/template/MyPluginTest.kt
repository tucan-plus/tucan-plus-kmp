package org.jetbrains.plugins.template

import com.intellij.ide.highlighter.XmlFileType
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.openapi.components.service
import com.intellij.openapi.vfs.findPsiFile
import com.intellij.psi.xml.XmlFile
import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.util.PsiErrorElementUtil
import org.jetbrains.plugins.template.services.MyProjectService

@TestDataPath("\$CONTENT_ROOT/src/test/testData")
class MyPluginTest : BasePlatformTestCase() {

    fun testFindSimilarities() {
        val directory = myFixture.copyDirectoryToProject("rename", "rename");
        val project = myFixture.project
        // code afterward is the same
        val files = directory.children
        Notification("Bagel", "Bagel was eaten ${files.contentToString()}", NotificationType.INFORMATION)
            .notify(project)
        for (file in files) {
            val psi = file.findPsiFile(project) as XmlFile

            Notification("Bagel", "psi $psi", NotificationType.INFORMATION)
                .notify(project)
        }
    }

    fun testXMLFile() {
        val psiFile = myFixture.configureByText(XmlFileType.INSTANCE, "<foo>bar</foo>")
        val xmlFile = assertInstanceOf(psiFile, XmlFile::class.java)

        assertFalse(PsiErrorElementUtil.hasErrors(project, xmlFile.virtualFile))

        assertNotNull(xmlFile.rootTag)

        xmlFile.rootTag?.let {
            assertEquals("foo", it.name)
            assertEquals("bar", it.value.text)
        }
    }

    fun testRename() {
        myFixture.testRename("foo.xml", "foo_after.xml", "a2")
    }

    fun testProjectService() {
        val projectService = project.service<MyProjectService>()

        assertNotSame(projectService.getRandomNumber(), projectService.getRandomNumber())
    }

    override fun getTestDataPath() = "src/test/testData/rename"
}
