package test.org.adams.opencms.parser

import org.adams.opencms.beans.ResourceType
import org.adams.opencms.beans.ResourceTypeProperty
import org.adams.opencms.parser.ResourceTypeXmlParser
import org.adams.opencms.parser.ResourceTypeXmlParserTrait
import org.junit.Assert

class TestResourceTypeXmlParser extends GroovyTestCase{

    final String xmlFile = "E:\\_DEV\\projects\\java\\gradle\\manifestbuilder\\src\\test\\resources\\vfs\\resourcetypes.xml"

    public void testParse() {
        File file = new File(xmlFile)
        ResourceTypeXmlParserTrait resourceTypeXmlParser = new ResourceTypeXmlParser()
        List<ResourceType> resourceTypeList = resourceTypeXmlParser.parseResourceTypes(file)

        ResourceType rt1 = resourceTypeList.get(0)
        ResourceTypeProperty prop = rt1.properties.get(0)
        Assert.assertEquals(1, resourceTypeList.size())
        Assert.assertEquals("org.opencms.file.types.CmsResourceTypeXmlContent", rt1.clazz)
        Assert.assertEquals("rebrushaftersales_campaign", rt1.name)
        Assert.assertEquals("113126", rt1.id)
        Assert.assertEquals(1, rt1.properties.size())

        Assert.assertEquals("template-elements", prop.name)
        Assert.assertEquals("shared", prop.type)
        Assert.assertEquals("/system/modules/com.bmw.master.rebrush/templates/pages/individual/owners/service/aftersales_campaign/aftersales_campaign.jsp", prop.value)

    }
}
