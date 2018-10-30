package org.adams.opencms.parser

import org.adams.opencms.beans.Parameter
import org.adams.opencms.beans.ResourceType
import org.adams.opencms.beans.ResourceTypeProperty

trait ResourceTypeXmlParserTrait implements XmlSnippetParser<ResourceType> {

    List<ResourceType> parseResourceTypes(File file) {
        String fileContents = file.getText('UTF-8')
        def resourcetypes = new XmlSlurper().parseText(fileContents)
        if (resourcetypes.type.collect().size() > 0) {
            List<ResourceType> resourceTypeList = new ArrayList<>()
            resourcetypes.type.findAll().each { it ->
                ResourceType resourceType = new ResourceType()
                resourceType.name = it.@name
                resourceType.clazz = it['@class']
                resourceType.id = it.@id
                if (resourcetypes.type.properties.property.collect().size() > 0) {
                    resourcetypes.type.properties.property.findAll().each { prop ->
                        ResourceTypeProperty property = new ResourceTypeProperty()
                        property.type = prop.value.@type.toString()
                        property.value = prop.value.toString()
                        property.name = prop.name.toString()
                        resourceType.properties.add(property)
                    }
                }
                if (it.param) {
                    Parameter parameter = new Parameter()
                    parameter.name = it.param.@name
                    parameter.value = it.param.text()
                    resourceType.param = parameter
                }
                resourceTypeList.add(resourceType)
            }
            return resourceTypeList
        }
        return null
    }
}
