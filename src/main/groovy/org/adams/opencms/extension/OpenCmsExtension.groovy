package org.adams.opencms.extension

import org.adams.opencms.beans.Dependency
import org.adams.opencms.beans.ExplorerType
import org.adams.opencms.beans.ExportPoint
import org.adams.opencms.beans.Info
import org.adams.opencms.beans.Module
import org.adams.opencms.beans.Parameter
import org.adams.opencms.beans.Resource
import org.adams.opencms.beans.ResourceType
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
    String openCmsVersion
    String userInstalled = user
    String exportVersion = "8"
    String info_project = "Online"
    Date dateCreated = new Date()
    String importScript = ""
    String group = ""
    List excludeResources = []
    String clazz = ''

    Module module
    Info info
    File modulePropertiesFile
    File dependencyXmlFile
    File explorerTypeXmlFile
    File exportPointXmlFile
    File parameterXmlFile
    File relationXmlFile
    File resourceXmlFile
    File resourceTypeXmlFile
    List<Dependency> dependencies
    List<ExplorerType> explorerTypes
    List<ExportPoint> exportPoints
    List<Parameter> parameters
    List<ResourceType> resourceTypes
    List<Resource> resources


    OpenCmsExtension(Project project, String moduleDir) {
        this.moduleDir = moduleDir
        if (this.opencmsExt('jarFileName') == '')
            this.jarFileName = project.name + '.jar'
    }

    def init() {
        this.modulePropertiesFile = project.file(this.modulePropertiesFileName)
        this.dependencyXmlFile = project.file(this.dependencyXmlFileName)
        this.resourceTypeXmlFile = project.file(this.resourceTypeXmlFileName)
        this.exportPointXmlFile = project.file(this.exportPointXmlFileName)
        this.explorerTypeXmlFile = project.file(this.explorerTypeXmlFileName)
        this.resourceXmlFile = project.file(this.resourceXmlFileName)
        this.parameterXmlFile = project.file(this.parameterXmlFileName)

        this.module = parseModuleProperties(modulePropertiesFile)
        this.info = parseInfoProperties(modulePropertiesFile)
        this.dependencies = parseDependencies(this.dependencyXmlFile)
        this.explorerTypes = parseExplorerTypes(this.explorerTypeXmlFile)
        this.exportPoints = parseExportPoints(this.exportPointXmlFile)
        this.resourceTypes =parseResourceTypes(this.resourceTypeXmlFile)
        this.resources = parseResources(this.resourceXmlFile)
    }


}
