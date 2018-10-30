package org.adams.opencms.parser

import org.adams.opencms.beans.Dependency

trait DependencyXmlParserTrait implements XmlSnippetParser<Dependency> {

    List<Dependency> parseDependencies(File file) {
        String fileContents = file.getText('UTF-8')
        def dependencies = new XmlSlurper().parseText(fileContents)
        if (dependencies.dependency.collect().size() > 0) {
            List<Dependency> dependenciesList = new ArrayList<>()
            dependencies.dependency.findAll().each { it ->
                Dependency dependency = new Dependency()
                dependency.name = it['@name']
                dependency.version = it['@version']
                dependenciesList.add(dependency)
            }
            return dependenciesList
        }
        return null
    }
}
