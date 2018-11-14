package test.org.adams.opencms.parser

import org.adams.opencms.beans.Dependency
import org.adams.opencms.parser.DependencyXmlParser
import org.adams.opencms.parser.DependencyXmlParserTrait
import org.junit.Assert

class TestDependencyXmlParser extends GroovyTestCase {

    final String xmlFile = "E:\\_DEV\\projects\\java\\gradle\\manifestbuilder\\src\\test\\resources\\vfs\\dependencies.xml"

    public void testParse() {
        DependencyXmlParserTrait dependencyXmlParser = new DependencyXmlParser()
        File file = new File(xmlFile)
        List<Dependency> dependencyList = dependencyXmlParser.parseDependencies(file);

        dependencyList.each { dep ->
            Assert.assertEquals("org.opencms.workplace", dep.name)
            Assert.assertEquals("10.0.0", dep.version)
        }
    }


}
