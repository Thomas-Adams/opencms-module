package org.adams.opencms.manifest

import groovy.xml.MarkupBuilder
import org.adams.opencms.beans.ExplorerAccessControl
import org.adams.opencms.beans.ExplorerAccessEntry
import org.adams.opencms.beans.ExplorerType
import org.adams.opencms.beans.NewResource

class ExplorerTypesXmlFromManifestParser extends SnippetXmlFromManifestParser {


    List<ExplorerType> parseManifestFile(File manifestFile) {
        String fileContents = manifestFile.getText('UTF-8')
        def export = new XmlSlurper().parseText(fileContents)
        List<ExplorerType> explorerTypeList = new ArrayList<>()
        if (export.module.explorertypes) {
            if (export.module.explorertypes.explorertype.collect().size() > 0) {
                export.module.explorertypes.explorertype.findAll().each { it ->
                    ExplorerType explorerType = new ExplorerType()
                    explorerType.name = it['@name']
                    explorerType.key = it['@key']
                    explorerType.icon = it['@icon']
                    explorerType.reference = it['@reference']

                    if (it.newresource) {
                        NewResource newResource = new NewResource()
                        newResource.page = it.newresource['@page']
                        newResource.uri = it.newresource['@uri']
                        newResource.info = it.newresource['@info']
                        newResource.order = Integer.parseInt(it.newresource['@order'].toString(), 10)
                        newResource.autosetnavigation = Boolean.valueOf(it.newresource['@autosetnavigation'].toString())
                        newResource.autosettitle = Boolean.valueOf(it.newresource['@autosettitle'].toString())
                        explorerType.newResource = newResource
                    }
                    if (it.accesscontrol) {
                        ExplorerAccessControl eac = new ExplorerAccessControl()
                        it.accesscontrol.accessentry.findAll().each { ace ->
                            ExplorerAccessEntry explorerAccessEntry = new ExplorerAccessEntry()
                            explorerAccessEntry.principal = ace.@principal
                            explorerAccessEntry.permissions = ace.@permissions
                            eac.accessEntries.add(explorerAccessEntry)
                        }
                        explorerType.accessControl = eac
                    }
                    explorerTypeList.add(explorerType)
                }
            }
        }
        return explorerTypeList
    }

    void writeExplorerTypesXml(File manifestFile) {
        File dir = manifestFile.getParentFile()
        List<ExplorerType> explorerTypes = parseManifestFile(manifestFile)
        String fileName = manifestFile.getParent() + File.separator + 'explorertypes.xml'
        File explorerTypesXml = new File(fileName)
        FileWriter fileWriter = new FileWriter(explorerTypesXml);
        def xml = new MarkupBuilder(fileWriter)
        xml.setDoubleQuotes(true)
        xml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")
        xml.explorertypes() {
            explorerTypes.each { ext ->
                explorertype(mane: ext.name, key: ext.key, icon: ext.icon, reference: ext.reference) {
                    ext.newResource.each { nr ->
                        newresource(uri: nr.uri, info: nr.info, page: nr.page, order: nr.order,
                                autosetnavigation: nr.autosetnavigation, autosettitle: nr.autosettitle)
                    }

                    ext.accessControl.each { acc ->
                        accesscontrol() {
                            acc.accessEntries.each { ace ->
                                accessentry(principal: ace.principal, permissions: ace.permissions)
                            }
                        }
                    }
                }
            }
        }
        fileWriter.close()
    }

}
