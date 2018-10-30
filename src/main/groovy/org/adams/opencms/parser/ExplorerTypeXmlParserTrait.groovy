package org.adams.opencms.parser

import org.adams.opencms.beans.ExplorerAccessControl
import org.adams.opencms.beans.ExplorerAccessEntry
import org.adams.opencms.beans.ExplorerType
import org.adams.opencms.beans.NewResource

trait ExplorerTypeXmlParserTrait implements XmlSnippetParser<ExplorerType> {

    List<ExplorerType> parseExplorerTypes(File file) {
        String fileContents = file.getText('UTF-8')
        def explorertypes = new XmlSlurper().parseText(fileContents)
        if (explorertypes.explorertype.collect().size() > 0) {
            List<ExplorerType> explorerTypeList = new ArrayList<>()
            explorertypes.explorertype.findAll().each { it ->
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
                if(it.accesscontrol) {
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
            return explorerTypeList
        }
        return null
    }
}

