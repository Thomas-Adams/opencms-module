package org.adams.opencms.parser


import org.adams.opencms.beans.Parameter

trait ParameterXmlParserTrait implements XmlSnippetParser<Parameter> {

    List<Parameter> parseParameters(File file) {
        String fileContents = file.getText('UTF-8')
        def parameters = new XmlSlurper().parseText(fileContents)
        if (parameters.param.collect().size() > 0) {
            List<Parameter> parametersList = new ArrayList<>()
            parameters.param.findAll().each { it ->
                Parameter parameter = new Parameter()
                parameter.name = it['@name']
                parameter.value = it.text()
                parametersList.add(parameter)
            }
            return parametersList
        }
        return null
    }
}
