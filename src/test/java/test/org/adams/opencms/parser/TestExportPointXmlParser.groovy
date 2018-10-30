package test.org.adams.opencms.parser

import org.adams.opencms.beans.ExportPoint
import org.adams.opencms.parser.ExportPointXmlParser
import org.adams.opencms.parser.ExportPointXmlParserTrait
import org.junit.Assert

class TestExportPointXmlParser extends GroovyTestCase {

    final String xmlFile = "E:\\_DEV\\projects\\java\\gradle\\manifestbuilder\\src\\test\\resources\\vfs\\exportpoints.xml"

    public void testParse() {
        File file = new File(xmlFile)
        ExportPointXmlParserTrait exportPointXmlParser = new ExportPointXmlParser()
        List<ExportPoint> exportPointList = exportPointXmlParser.parseExportPoints(file)

        ExportPoint exp1 = exportPointList.get(0)
        Assert.assertEquals(1,exportPointList.size())
        Assert.assertEquals("/system/modules/org.adams.opencms.synchron.module/classes/", exp1.uri)
        Assert.assertEquals("WEB-INF/classes/", exp1.destination)
    }
}
