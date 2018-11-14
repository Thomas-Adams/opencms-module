package test.org.adams.opencms.file

import org.adams.opencms.extension.OpenCmsExtension
import org.adams.opencms.file.CreateMissingMetaFiles
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder

class CreateMissingMetaFilesTest extends GroovyTestCase {

    void testCreateMissingMetaFiles() {

        Project project = ProjectBuilder.builder().build()
        OpenCmsExtension extension = new OpenCmsExtension(project, 'src/test/resources/vfs/')
        extension.moduleDir = 'E:\\_DEV\\projects\\java\\gradle\\opencms_module\\src\\test\\resources\\vfs3'
        File manifestFile = new File('E:\\_DEV\\projects\\java\\gradle\\opencms_module\\src\\test\\resources\\vfs3\\manifest.xml')
        File moduleDir  = new File(extension.moduleDir)

        CreateMissingMetaFiles createMissingMetaFiles = new CreateMissingMetaFiles()
        createMissingMetaFiles.createMissingMetaFiles(moduleDir)

    }
}
