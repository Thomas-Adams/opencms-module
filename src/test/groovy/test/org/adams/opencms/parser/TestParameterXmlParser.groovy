package test.org.adams.opencms.parser

import org.adams.opencms.beans.Parameter
import org.adams.opencms.parser.ParameterXmlParser
import org.adams.opencms.parser.ParameterXmlParserTrait
import org.junit.Assert

class TestParameterXmlParser extends GroovyTestCase {

    public final String xmlFile ="E:\\_DEV\\projects\\java\\gradle\\manifestbuilder\\src\\test\\resources\\vfs\\parameters.xml"

    public void testParse() {
        File file = new File(xmlFile)
        ParameterXmlParserTrait parameterXmlParser = new ParameterXmlParser()
        List<Parameter> parameters = parameterXmlParser.parseParameters(file)

        Assert.assertEquals(2, parameters.size())
        Parameter p1 = parameters.get(0)
        Assert.assertEquals("param1", p1.name)
        Assert.assertEquals("Hello", p1.value)

        Parameter p2 = parameters.get(1)
        Assert.assertEquals("param2", p2.name)
        Assert.assertEquals("World", p2.value)
    }
}
