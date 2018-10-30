package org.adams.opencms.extension

import org.adams.opencms.parser.*
import org.adams.opencms.tasks.AccessExtension

class OpenCmsExtension implements AccessExtension, ModulePropertiesReaderTrait, DependencyXmlParserTrait,
        ExplorerTypeXmlParserTrait, ExportPointXmlParserTrait, ParameterXmlParserTrait, RelationXmlParserTrait,
        ResourceTypeXmlParserTrait, ResourceXmlParserTrait {

    String moduleDir = 'src/main/vfs'
    String modulePropertiesFileName = "${moduleDir}/module.properties"
    String dependencyXmlFileName = "${moduleDir}/dependencies.xml"
    String explorerTypeXmlFileName = "${moduleDir}/explorertypes.xml"
    String exportPointXmlFileName = "${moduleDir}/exportpoints.xml"
    String parameterXmlFileName = "${moduleDir}/parameters.xml"
    String relationXmlFileName = "${moduleDir}/relations.xml"
    String resourceXmlFileName = "${moduleDir}/resources.xml"
    String resourceTypeXmlFileName = "${moduleDir}/resourcetypes.xml"
    boolean createResourceUUID = false
    boolean createStructureUUID = false
    boolean buildJar = true
    String moduleName
    String moduleVersion

}
