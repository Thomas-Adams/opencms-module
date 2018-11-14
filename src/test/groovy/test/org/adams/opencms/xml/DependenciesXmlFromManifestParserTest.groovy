package test.org.adams.opencms.xml

import org.adams.opencms.extension.OpenCmsExtension
import org.adams.opencms.manifest.DependenciesXmlFromManifestParser
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder

class DependenciesXmlFromManifestParserTest extends GroovyTestCase {


    void testWriteDependenciesXML() {
        Project project = ProjectBuilder.builder().build()
        OpenCmsExtension extension = new OpenCmsExtension(project, 'src/test/resources/vfs/')
        extension.moduleDir = 'E:\\_DEV\\projects\\java\\gradle\\opencms_module\\src\\test\\resources\\vfs2'
        File manifestFile = new File('E:\\_DEV\\projects\\java\\gradle\\opencms_module\\src\\test\\resources\\vfs2\\manifest.xml')

        DependenciesXmlFromManifestParser parser = new DependenciesXmlFromManifestParser()
        parser.writeDependenciesXml(manifestFile)
    }


}
