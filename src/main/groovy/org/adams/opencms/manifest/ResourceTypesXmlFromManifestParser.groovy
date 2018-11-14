package org.adams.opencms.manifest

import groovy.xml.MarkupBuilder
import org.adams.opencms.beans.Parameter
import org.adams.opencms.beans.ResourceType
import org.adams.opencms.beans.ResourceTypeProperty

class ResourceTypesXmlFromManifestParser extends SnippetXmlFromManifestParser {


    List<ResourceType> parseManifestFile(File manifestFile) {
        String fileContents = manifestFile.getText('UTF-8')
        def export = new XmlSlurper().parseText(fileContents)
        List<ResourceType> resourceTypes = new ArrayList<>()
        if (export.module.resourcetypes) {
            if (export.module.resourcetypes.type.collect().size() > 0) {
                export.module.resourcetypes.type.findAll().each { it ->
                    ResourceType resourceType = new ResourceType()
                    resourceType.clazz = it['@class']
                    resourceType.name = it['@name']
                    resourceType.id = it['@id']
                    it.param.each { par ->
                        Parameter parameter = null
                        parameter = new Parameter()
                        parameter.name = par['@name']
                        println('param value ' + par)
                        println('param value ' + par.text())
                        parameter.value = par.text()
                        resourceType.params.add(parameter)
                    }
                    it.properties.property.findAll().each { prop ->
                        ResourceTypeProperty resourceTypeProperty = new ResourceTypeProperty()
                        resourceTypeProperty.name = prop.name.text()
                        resourceTypeProperty.value = prop.value.text()
                        resourceType.properties.add(resourceTypeProperty)
                    }
                    resourceTypes.add(resourceType)
                }
            }
        }
        return resourceTypes
    }

    void writeResourceTypesXml(File manifestFile) {
        File dir = manifestFile.getParentFile()
        List<ResourceType> resourceTypes = parseManifestFile(manifestFile)
        String fileName = manifestFile.getParent() + File.separator + 'resourcetypes.xml'
        File resourceTypesXml = new File(fileName)
        FileWriter fileWriter = new FileWriter(resourceTypesXml);
        def xml = new MarkupBuilder(fileWriter)
        xml.setDoubleQuotes(true)
        xml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")
        xml.resourcetypes() {
            resourceTypes.each { rt ->
                'type'('class': rt.clazz, name: rt.name, id: rt.id) {
                    rt.params.each { par ->
                        param(name: par.name, par.value)
                    }
                    if(rt.properties.size()>0) {
                        properties() {
                            rt.properties.each { pr ->
                                property() {
                                    name( pr.name)
                                    value() {
                                        mkp.yieldUnescaped "<![CDATA[${pr.value}]]>"
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }
        fileWriter.close()
    }

}
