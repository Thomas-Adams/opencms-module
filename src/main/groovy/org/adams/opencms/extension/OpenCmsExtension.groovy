package org.adams.opencms.extension

import org.adams.opencms.parser.*
import org.adams.opencms.tasks.AccessExtension
import org.gradle.api.Project

class OpenCmsExtension implements AccessExtension, ModulePropertiesReaderTrait, DependencyXmlParserTrait,
        ExplorerTypeXmlParserTrait, ExportPointXmlParserTrait, ParameterXmlParserTrait,
        ResourceTypeXmlParserTrait, ResourceXmlParserTrait {

    String moduleDir = 'src/main/vfs'
    String manifestFileName = 'manifest.xml'
    String modulePropertiesFileName = "${moduleDir}/module.properties"
    String dependencyXmlFileName = "${moduleDir}/dependencies.xml"
    String explorerTypeXmlFileName = "${moduleDir}/explorertypes.xml"
    String exportPointXmlFileName = "${moduleDir}/exportpoints.xml"
    String parameterXmlFileName = "${moduleDir}/parameters.xml"
    String relationXmlFileName = "${moduleDir}/relations.xml"
    String resourceXmlFileName = "${moduleDir}/resources.xml"
    String resourceTypeXmlFileName = "${moduleDir}/resourcetypes.xml"
    boolean createResourceUUID = false
    boolean createStructureUUID = true
    boolean buildJar = true
    String jarFileName = ''
    boolean createMetaInfoOnFly = true
    String moduleName
    String moduleVersion
    String infoproject = "Online"
    String user = "Admin"


    OpenCmsExtension(Project project, String moduleDir) {
        this.moduleDir = moduleDir
        if (this.opencmsExt('jarFileName') == '')
            this.jarFileName = project.name + '.jar'
    }
}
