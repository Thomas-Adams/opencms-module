package org.adams.opencms.parser


import org.adams.opencms.beans.Resource

trait ResourceXmlParserTrait implements XmlSnippetParser<Resource> {

    List<Resource> parseResources(File file) {
        String fileContents = file.getText('UTF-8')
        def resources = new XmlSlurper().parseText(fileContents)
        if (resources.resource.collect().size() > 0) {
            List<Resource> resourcesList = new ArrayList<>()
            resources.resource.findAll().each { it ->
                Resource resource = new Resource()
                resource.uri = it.@uri
                resourcesList.add(resource)
            }
            return resourcesList
        }
        return null
    }
}
