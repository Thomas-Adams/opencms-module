package test.org.adams.opencms.xml


import org.adams.opencms.extension.OpenCmsExtension
import org.adams.opencms.manifest.ManifestXmlParser
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder

class ManifestParserTest extends GroovyTestCase {


    void testParseExistingManifestFile() {

        Project project = ProjectBuilder.builder().build()
        OpenCmsExtension extension = new OpenCmsExtension(project, 'src/test/resources/vfs/')
        extension.moduleDir = 'E:\\_DEV\\projects\\java\\gradle\\opencms_module\\src\\test\\resources\\vfs'

        File manifestFile = new File('E:\\_DEV\\projects\\java\\gradle\\opencms_module\\src\\test\\resources\\org.adams.opencms.synchron.module_1.0.0\\manifest.xml')
        ManifestXmlParser manifestXmlParser = new ManifestXmlParser(extension)
        manifestXmlParser.createMetaFiles(manifestFile)

    }

}
