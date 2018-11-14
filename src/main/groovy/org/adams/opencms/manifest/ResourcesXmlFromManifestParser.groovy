package org.adams.opencms.manifest

import groovy.xml.MarkupBuilder
import org.adams.opencms.beans.Resource

class ResourcesXmlFromManifestParser extends SnippetXmlFromManifestParser {


    List<Resource> parseManifestFile(File manifestFile) {
        String fileContents = manifestFile.getText('UTF-8')
        def export = new XmlSlurper().parseText(fileContents)
        List<Resource> resources = new ArrayList<>()
        if (export.module.resources) {
            if (export.module.resources.resource.collect().size() > 0) {
                export.module.resources.resource.findAll().each { it ->
                    Resource resource = new Resource()
                    resource.uri = it['@uri']

                    resources.add(resource)
                }
            }
        }
        return resources
    }

    void writeResourcesXml(File manifestFile) {
        File dir = manifestFile.getParentFile()
        List<Resource> resources = parseManifestFile(manifestFile)
        String fileName = manifestFile.getParent() + File.separator + 'resources.xml'
        File resourcesXml = new File(fileName)
        FileWriter fileWriter = new FileWriter(resourcesXml);
        def xml = new MarkupBuilder(fileWriter)
        xml.setDoubleQuotes(true)
        xml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")
        xml.resources() {
            resources.each { res -> resource(uri: res.uri)}
        }
        fileWriter.close()
    }

}
