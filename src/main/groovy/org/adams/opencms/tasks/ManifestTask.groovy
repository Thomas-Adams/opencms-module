package org.adams.opencms.tasks

import groovy.xml.MarkupBuilder
import org.adams.opencms.beans.*
import org.adams.opencms.file.ModuleFileHandler
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

import java.text.SimpleDateFormat

class ManifestTask extends OpenCmsPluginTask implements AccessExtension {

    Manifest manifest = new Manifest()
    Module module
    ModuleFileHandler moduleFileHandler = new ModuleFileHandler()
    List<ModuleFile> moduleFiles;

    @OutputFile
    File getManifestFile() {
        return manifestFile
    }


    void init() {
        List<Relation> relations = new ArrayList<>()
        List<ExportPoint> exportPoints = new ArrayList<>()
        List<Resource> resources = new ArrayList<>()
        List<Dependency> dependencies = new ArrayList<>()
        List<ResourceType> resourceTypes = new ArrayList<>()
        List<ExplorerType> explorerTypes = new ArrayList<>()
        List<Parameter> parameters = new ArrayList<>()
        module = new Module()
        Info info = new Info()
        project = getProject()

        File modulePropertiesFile = project.file(this.extension.modulePropertiesFileName)
        File dependencyXmlFile = project.file(this.extension.dependencyXmlFileName)
        File explorerTypeXmlFile = project.file(this.extension.explorerTypeXmlFileName)
        File exportPointXmlFile = project.file(this.extension.exportPointXmlFileName)
        File parameterXmlFile = project.file(this.extension.parameterXmlFileName)
        File resourceTypeXmlFile = project.file(this.extension.resourceTypeXmlFileName)
        File resourceXmlFile = project.file(this.extension.resourceXmlFileName)


        module = this.extension.parseModuleProperties(modulePropertiesFile)
        module.info = this.extension.parseInfoProperties(modulePropertiesFile)
        module.exportPoints = this.extension.parseExportPoints(exportPointXmlFile)
        module.resources = this.extension.parseResources(resourceXmlFile)
        module.dependencies = this.extension.parseDependencies(dependencyXmlFile)
        module.resourceTypes = this.extension.parseResourceTypes(resourceTypeXmlFile)
        module.explorerTypes = this.extension.parseExplorerTypes(explorerTypeXmlFile)
        module.parameters = this.extension.parseParameters(parameterXmlFile)


        moduleFileHandler = new ModuleFileHandler(this.moduleDir)
        moduleFileHandler.openCmsExtension = this.extension
        moduleFiles = moduleFileHandler.initModuleFiles()
        manifest.info = module.info
        manifest.module = module
        manifest.moduleFiles = new ModuleFiles()
        manifest.moduleFiles.setFiles(moduleFiles)
        debugModuleFiles()
    }


    void debugModuleFiles() {
        manifest.moduleFiles.files.each { f ->
            logger.debug("destination : " + f.destination)
        }
    }


    @TaskAction
    void generate() {
        println("ManifestTask generate something ....")
        println(moduleDir.getAbsolutePath())
        println("Is existing? : " + moduleDir.exists())
        println("Is directory? : " + moduleDir.isDirectory())
        init()
        createManifestFile()

    }


    def createManifestFile() {
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z")
        def root = "<export></export>"
        if (!manifestFile.exists()) {
            manifestFile.parentFile.mkdirs()
            manifestFile.createNewFile()
        }
        FileWriter writer = new FileWriter(manifestFile)
        def builder = new MarkupBuilder(writer)
        builder.setDoubleQuotes(true)

        Info info1 = manifest.info
        Module module1 = manifest.module


        builder.export() {
            info() {
                opencms_version(info1.opencms_version)
                createdate(formatter.format(info1.createDate))
                infoproject({ getMkp().yieldUnescaped("<![CDATA[" + info1.info_project + "]]>") })
                export_version(info1.export_version)
            }

            module() {
                name(module1.name)
                nicename(module1.nicename)
                group(module1.group)
                "class"(module1.clazz)
                "import-script"(module1.importScript)
                "export-mode"(module1.exportMode)
                description({ getMkp().yieldUnescaped("<![CDATA[" + module1.description + "]]>") })
                version(module1.version)
                authorname({ getMkp().yieldUnescaped("<![CDATA[" + module1.authorname + "]]>") })
                authoremail({ getMkp().yieldUnescaped("<![CDATA[" + module1.authoremail + "]]>") })
                datecreated(formatter.format(module1.dateCreated))
                userinstalled(module1.userInstalled)
                dateinstalled(formatter.format(module1.dateInstalled))
                dependencies {
                    module1.dependencies.each { e ->
                        dependency(name:e.name, version: e.version)
                    }
                }
                exportpoints {
                    module1.exportPoints.each {
                        e ->
                            exportpoint(uri: e.uri, destination: e.destination)
                    }
                }
                resources {
                    module1.resources.each {
                        e ->
                            ("resource"(uri: e.uri) {
                            })
                    }
                }
                excluderesources {
                    module1.excludeResources.each {
                        e ->
                            ("excluderesource"(uri: e.uri) {
                            })
                    }
                }
                parameters {
                    module1.parameters.each {
                        e -> parameter(name: e.name, e.value)
                    }
                }
            }

            files {
                manifest.moduleFiles.files.each { f ->
                    println("modulefile " + f)
                    file {
                        destination(f.destination)
                        type(f.type)
                        if (getOpencmsExtension().createStructureUUID) {
                            uuidstructure(f.uuidStructure)
                        }
                        if (getOpencmsExtension().createResourceUUID) {
                            uuidresource(f.uuidResource)
                        }
                        datelastmodified(f.dateLastModified)
                        userlastmodified(f.userLastModified)
                        datecreated(f.dateCreated)
                        usercreated(f.userCreated)
                        flags(f.flags)
                        properties {
                            f.properties.each { prop ->
                                if (prop.type == PropertyType.SIMPLE) {
                                    property {
                                        name {
                                            prop.key
                                        }
                                        value({ getMkp().yieldUnescaped("<![CDATA[" + prop.value + "]]>") })
                                    }
                                } else {
                                    property(type: "shared") {
                                        name {
                                            prop.key
                                        }
                                        value({ getMkp().yieldUnescaped("<![CDATA[" + prop.value + "]]>") })
                                    }
                                }
                            }
                        }

                        relations {
                            f.relations.each { rel ->
                                relation(element: rel.element, type: rel.type, invalidate: rel.invalidate)
                            }
                        }
                        accesscontrol {
                            if (f.accessControl) {
                                f.accessControl.accessEntries.each { ace ->
                                    accessentry {
                                        uuidprincipal {
                                            ace.principal
                                        }
                                        flags {
                                            ace.flags
                                        }
                                        permissionset {
                                            allowed {
                                                ace.permissionSet.allowed
                                            }
                                            denied {
                                                ace.permissionSet.denied
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        writer.flush()
        writer.close()
    }
}
