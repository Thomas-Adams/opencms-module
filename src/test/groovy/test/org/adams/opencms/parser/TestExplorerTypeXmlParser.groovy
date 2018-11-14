package test.org.adams.opencms.parser

import org.adams.opencms.beans.ExplorerAccessEntry
import org.adams.opencms.beans.ExplorerType
import org.adams.opencms.parser.ExplorerTypeXmlParser
import org.adams.opencms.parser.ExplorerTypeXmlParserTrait
import org.junit.Assert

class TestExplorerTypeXmlParser extends GroovyTestCase {

    final String xmlFile = "E:\\_DEV\\projects\\java\\gradle\\manifestbuilder\\src\\test\\resources\\vfs\\explorertypes.xml"

    public void testParse() {
        File file = new File(xmlFile)
        ExplorerTypeXmlParserTrait explorerTypeXmlParser = new ExplorerTypeXmlParser()
        List<ExplorerType> explorerTypeList = explorerTypeXmlParser.parseExplorerTypes(file)

        ExplorerType et1 = explorerTypeList.get(0);
        Assert.assertEquals("bmw_master_rebrush", et1.name)
        Assert.assertEquals("com.fileicon.bmw_master_rebrush", et1.key)
        Assert.assertEquals("xmlcontent.gif", et1.icon)
        Assert.assertEquals("xmlcontent", et1.reference)
        Assert.assertEquals("newresource.jsp?page=bmw_master_rebrush", et1.newResource.uri)
        Assert.assertEquals("com.desc.bmw_master_rebrush", et1.newResource.info)
        Assert.assertEquals(115, et1.newResource.order)
        Assert.assertEquals(false, et1.newResource.autosettitle)
        Assert.assertEquals(false, et1.newResource.autosetnavigation)


        ExplorerType et2 = explorerTypeList.get(1);
        Assert.assertEquals("rebrushhomepage", et2.name)
        Assert.assertEquals("fileicon.rebrushhomepage", et2.key)
        Assert.assertEquals("xmlcontent.gif", et2.icon)
        Assert.assertEquals("xmlcontent", et2.reference)
        Assert.assertEquals("bmw_master_rebrush", et2.newResource.page)
        Assert.assertEquals("newresource_xmlcontent.jsp?newresourcetype=rebrushhomepage", et2.newResource.uri)
        Assert.assertEquals(120048, et2.newResource.order)
        Assert.assertEquals(false, et2.newResource.autosettitle)
        Assert.assertEquals(false, et2.newResource.autosetnavigation)
        Assert.assertEquals(3, et2.accessControl.accessEntries.size())


        Assert.assertNotNull(et2.accessControl)
        List<ExplorerAccessEntry> accessEntries = et2.accessControl.accessEntries
        ExplorerAccessEntry ace1 = accessEntries.get(0)
        Assert.assertEquals("GROUP.Administrators", ace1.principal)
        Assert.assertEquals("+r+v+w+c", ace1.permissions)




    }
}
