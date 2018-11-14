package org.adams.opencms.manifest

import groovy.xml.MarkupBuilder
import org.adams.opencms.beans.Parameter

class ParametersXmlFromManifestParser extends SnippetXmlFromManifestParser {


    List<Parameter> parseManifestFile(File manifestFile) {
        String fileContents = manifestFile.getText('UTF-8')
        def export = new XmlSlurper().parseText(fileContents)
        List<Parameter> parameters = new ArrayList<>()
        if (export.module.parameters) {
            if (export.module.paramaters.parameter.collect().size() > 0) {
                export.module.paramaters.parameter.findAll().each { it ->
                    Parameter param = new Parameter()
                    param.name = it['@name']
                    param.value = it['@value']
                    parameters.add(param)
                }

            }
        }
        return parameters
    }

    void writeParametersXml(File manifestFile) {
        File dir = manifestFile.getParentFile()
        List<Parameter> parameters = parseManifestFile(manifestFile)
        String fileName = manifestFile.getParent() + File.separator + 'parameters.xml'
        File parametersXml = new File(fileName)
        FileWriter fileWriter = new FileWriter(parametersXml);
        def xml = new MarkupBuilder(fileWriter)
        xml.setDoubleQuotes(true)
        xml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")
        xml.parameters() {
            parameters.each { par ->
                param(name: par.name, par.value)
            }
        }
        fileWriter.close()
    }

}
