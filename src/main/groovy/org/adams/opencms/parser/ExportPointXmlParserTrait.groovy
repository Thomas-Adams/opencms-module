package org.adams.opencms.parser

import org.adams.opencms.beans.ExportPoint

trait ExportPointXmlParserTrait implements XmlSnippetParser<ExportPoint> {

    List<ExportPoint> parseExportPoints(File file) {
        String fileContents = file.getText('UTF-8')
        def exportpoints = new XmlSlurper().parseText(fileContents)
        if (exportpoints.exportpoint.collect().size() > 0) {
            List<ExportPoint> exportPointList = new ArrayList<>()
            exportpoints.exportpoint.findAll().each { it ->
                ExportPoint exportPoint = new ExportPoint()
                exportPoint.uri = it.@uri
                exportPoint.destination = it.@destination
                exportPointList.add(exportPoint)
            }
            return exportPointList
        }
        return null
    }
}
