package de.selfmade4u

import com.intellij.openapi.application.PathManager
import com.intellij.openapi.module.Module
import com.intellij.openapi.projectRoots.JavaSdk
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.roots.ContentEntry
import com.intellij.openapi.roots.ModifiableRootModel
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.roots.impl.libraries.LibraryEx
import com.intellij.openapi.roots.ui.configuration.libraryEditor.NewLibraryEditor
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.testFramework.LightProjectDescriptor
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase5
import org.junit.jupiter.api.Test
import java.io.File

// https://github.com/JetBrains/intellij-community/blob/master/plugins/kotlin/test-framework/test/org/jetbrains/kotlin/idea/test/kmp/KMPNativeLinuxProjectDescriptor.kt
// https://github.com/JetBrains/intellij-community/blob/4192b57a80be69fb8901c5bbc3adf393285c432d/plugins/kotlin/test-framework/test/org/jetbrains/kotlin/idea/test/KotlinLightProjectDescriptor.java#L19

// https://github.com/JetBrains/kotlin/blob/master/analysis/stubs/tests/org/jetbrains/kotlin/analysis/decompiler/BuiltinsDecompilerTest.kt

// https://github.com/JetBrains/intellij-structural-search-for-kotlin/blob/master/src/test/kotlin/com/jetbrains/kotlin/structuralsearch/KotlinLightProjectDescriptor.kt
open class KotlinLightProjectDescriptor : LightProjectDescriptor() {
    override fun getSdk(): Sdk? {
        val javaHome = System.getProperty("java.home")
        assert(File(javaHome).isDirectory)
        val table = ProjectJdkTable.getInstance()
        val existing = table.findJdk("Full JDK")
        return existing ?: JavaSdk.getInstance().createJdk("Full JDK", javaHome, true)
    }

    protected fun addLibrary(clazz: Class<*>, orderRootType: OrderRootType, model: ModifiableRootModel) {
        val editor = NewLibraryEditor()
        editor.name = clazz.canonicalName

        val file = File(PathManager.getJarPathForClass(clazz) ?: "")
        assert(file.exists())
        editor.addRoot(
            VfsUtil.getUrlForLibraryRoot(file),
            orderRootType
        )

        val libraryTableModifiableModel = model.moduleLibraryTable.modifiableModel
        val library = libraryTableModifiableModel.createLibrary(editor.name)

        val libModel = library.modifiableModel
        editor.applyTo(libModel as LibraryEx.ModifiableModelEx)

        libModel.commit()
        libraryTableModifiableModel.commit()
    }

    override fun configureModule(module: Module, model: ModifiableRootModel, contentEntry: ContentEntry) {
        addLibrary(AccessDeniedException::class.java, OrderRootType.CLASSES, model)
    }
}

// https://plugins.jetbrains.com/docs/intellij/intellij-platform-extension-point-list.html
// https://github.com/JetBrains/intellij-community/blob/master/platform/analysis-api/src/com/intellij/codeInspection/GlobalInspectionTool.java

// https://github.com/JetBrains/intellij-community/tree/master/platform/testFramework/junit5/test/showcase
class MyPluginTest : LightJavaCodeInsightFixtureTestCase5(KotlinLightProjectDescriptor()) {

    @Test
    fun testFindSimilarities() {
        //Registry.get("platform.random.idempotence.check.rate").setValue(1, getTestRootDisposable())
        fixture.copyDirectoryToProject("", "");
        fixture.testHighlighting("HtmlParsing.kt")
        fixture.testHighlighting("main.kt")
    }

    override fun getTestDataPath() = "src/test/testData/simple"
}
