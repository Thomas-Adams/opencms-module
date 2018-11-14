package org.adams.opencms.manifest

import groovy.xml.MarkupBuilder
import org.adams.opencms.beans.ExportPoint

class ExportPointsXmlFromManifestParser extends SnippetXmlFromManifestParser {


    List<ExportPoint> parseManifestFile(File manifestFile) {
        String fileContents = manifestFile.getText('UTF-8')
        def export = new XmlSlurper().parseText(fileContents)
        List<ExportPoint> exportPoints = new ArrayList<>()
        if (export.module.exportpoints) {
            if (export.module.exportpoints.exportpoint.collect().size() > 0) {
                export.module.exportpoints.exportpoint.findAll().each { it ->
                    ExportPoint exportPoint = new ExportPoint()
                    exportPoint.uri = it['@uri']
                    exportPoint.destination = it['@destination']
                    exportPoints.add(exportPoint)
                }
            }
        }
        return exportPoints
    }

    void writeExportpointsXml(File manifestFile) {
        File dir = manifestFile.getParentFile()
        List<ExportPoint> exportPoints = parseManifestFile(manifestFile)
        String fileName = manifestFile.getParent() + File.separator + 'exportpoints.xml'
        File exportPointsXml = new File(fileName)
        FileWriter fileWriter = new FileWriter(exportPointsXml);
        def xml = new MarkupBuilder(fileWriter)
        xml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")
        xml.setDoubleQuotes(true)
        xml.exportpoints() {
            exportPoints.each { exp -> exportpoint(uri: exp.uri, destination: exp.destination) }
        }
        fileWriter.close()
    }

}
