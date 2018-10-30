package test.org.adams.opencms.parser


import org.adams.opencms.beans.Relation
import org.adams.opencms.parser.RelationXmlParser
import org.adams.opencms.parser.RelationXmlParserTrait
import org.junit.Assert;

public class TestRelationXmlParser extends GroovyTestCase {

    final String xmlFile = "E:\\_DEV\\projects\\java\\gradle\\manifestbuilder\\src\\test\\resources\\vfs\\relations.xml"

    public void testParse() {
        File file = new File(xmlFile)
        RelationXmlParserTrait relationXmlParser = new RelationXmlParser()
        List<Relation> relations = relationXmlParser.parseRelations(file)

        Relation rel1 = relations.get(0)
        Assert.assertEquals(1, relations.size())
        Assert.assertEquals("STRONG", rel1.type.toString())
        Assert.assertEquals("element", rel1.element)
        Assert.assertEquals("false", rel1.invalidate)
    }
}


