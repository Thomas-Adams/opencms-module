package test.org.adams.opencms.xml

import org.adams.opencms.extension.OpenCmsExtension
import org.adams.opencms.manifest.ExplorerTypesXmlFromManifestParser
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder

class ExplorerTypesXmlFromManifestParserTest extends GroovyTestCase {

    void testWriteExplorertypesXml() {

        Project project = ProjectBuilder.builder().build()
        OpenCmsExtension extension = new OpenCmsExtension(project, 'src/test/resources/vfs/')
        extension.moduleDir = 'E:\\_DEV\\projects\\java\\gradle\\opencms_module\\src\\test\\resources\\vfs2'
        File manifestFile = new File('E:\\_DEV\\projects\\java\\gradle\\opencms_module\\src\\test\\resources\\vfs2\\manifest1.xml')

        ExplorerTypesXmlFromManifestParser parser = new ExplorerTypesXmlFromManifestParser()
        parser.writeExplorerTypesXml(manifestFile)


    }
}
