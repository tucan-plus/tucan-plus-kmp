package de.selfmade4u

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
import de.selfmade4u.services.MyProjectService

// https://plugins.jetbrains.com/docs/intellij/intellij-platform-extension-point-list.html
// https://github.com/JetBrains/intellij-community/blob/master/platform/analysis-api/src/com/intellij/codeInspection/GlobalInspectionTool.java

@TestDataPath("\$CONTENT_ROOT/src/test/testData")
class MyPluginTest : BasePlatformTestCase() {

    fun testFindSimilarities() {
        val directory = myFixture.copyDirectoryToProject("", "");
        val project = myFixture.project
        Extractor().process(project, null, null)
    }

    override fun getTestDataPath() = "src/test/testData/rename"
}
