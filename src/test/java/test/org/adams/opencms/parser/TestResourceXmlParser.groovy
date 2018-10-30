package test.org.adams.opencms.parser

import org.adams.opencms.beans.Resource
import org.adams.opencms.parser.ResourceXmlParser
import org.adams.opencms.parser.ResourceXmlParserTrait
import org.junit.Assert

class TestResourceXmlParser extends GroovyTestCase {

    final String xmlFile = "E:\\_DEV\\projects\\java\\gradle\\manifestbuilder\\src\\test\\resources\\vfs\\resources.xml"

    public void testParse() {
        File file = new File(xmlFile)
        ResourceXmlParserTrait resourceXmlParser = new ResourceXmlParser()
        List<Resource> resourceList = resourceXmlParser.parseResources(file)

        Resource r1 = resourceList.get(0)
        Assert.assertEquals(7, resourceList.size())
        Assert.assertEquals("/system/workplace/commons/list-csv.jsp", r1.uri)

    }


}
