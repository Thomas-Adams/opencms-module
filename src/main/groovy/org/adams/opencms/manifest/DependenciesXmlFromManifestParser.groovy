package org.adams.opencms.manifest

import groovy.xml.MarkupBuilder
import org.adams.opencms.beans.Dependency

class DependenciesXmlFromManifestParser extends SnippetXmlFromManifestParser {


    List<Dependency> parseManifestFile(File manifestFile) {
        String fileContents = manifestFile.getText('UTF-8')
        def export = new XmlSlurper().parseText(fileContents)
        List<Dependency> dependencies = new ArrayList<>()
        if (export.module.dependencies) {
            if (export.module.dependencies.dependency.collect().size() > 0) {
                export.module.dependencies.dependency.findAll().each { it ->
                    Dependency dep = new Dependency()
                    dep.name = it['@name']
                    dep.version = it['@version']
                    dependencies.add(dep)
                }
            }
        }
        return dependencies
    }

    void writeDependenciesXml(File manifestFile) {
        File dir = manifestFile.getParentFile()
        List<Dependency> dependencies = parseManifestFile(manifestFile)
        String fileName = manifestFile.getParent() + File.separator + 'dependencies.xml'
        File dependenciesXml = new File(fileName)
        FileWriter fileWriter = new FileWriter(dependenciesXml);
        def xml = new MarkupBuilder(fileWriter)
        xml.setDoubleQuotes(true)
        xml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")
        xml.dependencies() {
            dependencies.each { dep -> dependency(name: dep.name, version: dep.version) }
        }
        fileWriter.close()
    }

}
