package org.adams.opencms.parser

import org.adams.opencms.beans.Relation
import org.adams.opencms.beans.RelationTypes

trait RelationXmlParserTrait implements XmlSnippetParser<RelationTypes> {

    List<Relation> parseRelations(File file) {
        String fileContents = file.getText('UTF-8')
        def relations = new XmlSlurper().parseText(fileContents)
        if (relations.relation.collect().size() > 0) {
            List<Relation> relationsList = new ArrayList<>()
            relations.relation.findAll().each { it ->
                Relation relation = new Relation()
                String t = it.@type
                if (t.toUpperCase().equals("STRONG")) {
                    relation.type = RelationTypes.STRONG
                } else {
                    relation.type = RelationTypes.WEAK
                }
                relation.invalidate = it.@invalidate
                relation.element = it.@element
                relationsList.add(relation)
            }
            return relationsList
        }
        return null
    }
}
