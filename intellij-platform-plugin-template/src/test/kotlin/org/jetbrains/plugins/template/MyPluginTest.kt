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
import de.selfmade4u.magicFunction
import org.jetbrains.plugins.template.services.MyProjectService

// https://plugins.jetbrains.com/docs/intellij/intellij-platform-extension-point-list.html
// https://github.com/JetBrains/intellij-community/blob/master/platform/analysis-api/src/com/intellij/codeInspection/GlobalInspectionTool.java

@TestDataPath("\$CONTENT_ROOT/src/test/testData")
class MyPluginTest : BasePlatformTestCase() {

    fun testFindSimilarities() {
        val directory = myFixture.copyDirectoryToProject("", "");
        val project = myFixture.project
        magicFunction(directory, project)
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
