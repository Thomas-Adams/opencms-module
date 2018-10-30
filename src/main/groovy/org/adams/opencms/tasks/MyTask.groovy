package org.adams.opencms.tasks

import groovy.xml.MarkupBuilder
import org.adams.opencms.beans.*
import org.adams.opencms.extension.OpenCmsExtension
import org.adams.opencms.file.ModuleFileHandler
import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

import java.text.SimpleDateFormat

class MyTask extends DefaultTask implements AccessExtension {

    Manifest manifest = new Manifest()
    Module module
    ModuleFileHandler moduleFileHandler = new ModuleFileHandler()
    List<ModuleFile> moduleFiles;

    File manifestFile
    File moduleDir
    File sourceDir

    @Input
    File getSourceDir() {
        return sourceDir
    }

    void setSourceDir(File sourceDir) {
        this.sourceDir = sourceDir
    }

    @InputDirectory
    File getModuleDir() {
        return moduleDir
    }

    void setModuleDir(File moduleDir) {
        this.moduleDir = moduleDir
    }

    @OutputFile
    File getManifestFile() {
        return manifestFile
    }

    void setManifestFile(File manifestFile) {
        this.manifestFile = manifestFile
    }


    void init(OpenCmsExtension opencms) {
        List<Relation> relations = new ArrayList<>()
        List<ExportPoint> exportPoints = new ArrayList<>()
        List<Resource> resources = new ArrayList<>()
        List<Dependency> dependencies = new ArrayList<>()
        List<ResourceType> resourceTypes = new ArrayList<>()
        List<ExplorerType> explorerTypes = new ArrayList<>()
        List<Parameter> parameters = new ArrayList<>()
        module = new Module()
        Info info = new Info()


        File modulePropertiesFile = project.file(opencms.modulePropertiesFileName)
        File dependencyXmlFile = project.file(opencms.dependencyXmlFileName)
        File explorerTypeXmlFile = project.file(opencms.explorerTypeXmlFileName)
        File exportPointXmlFile = project.file(opencms.exportPointXmlFileName)
        File parameterXmlFile = project.file(opencms.parameterXmlFileName)
        File relationXmlFile = project.file(opencms.relationXmlFileName)
        File resourceTypeXmlFile = project.file(opencms.resourceTypeXmlFileName)
        File resourceXmlFile = project.file(opencms.resourceXmlFileName)


        module = opencms.parseModuleProperties(modulePropertiesFile)
        module.info = opencms.parseInfoProperties(modulePropertiesFile)
        module.relations = opencms.parseRelations(relationXmlFile)
        module.exportPoints = opencms.parseExportPoints(exportPointXmlFile)
        module.resources = opencms.parseResources(resourceXmlFile)
        module.dependencies = opencms.parseDependencies(dependencyXmlFile)
        module.resourceTypes = opencms.parseResourceTypes(resourceTypeXmlFile)
        module.explorerTypes = opencms.parseExplorerTypes(explorerTypeXmlFile)
        module.parameters = opencms.parseParameters(parameterXmlFile)


        moduleFileHandler = new ModuleFileHandler(sourceDir)
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
        println("MyTask generate something ....")
        println(moduleDir.getAbsolutePath())
        println("Is existing? : " + moduleDir.exists())
        println("Is directory? : " + moduleDir.isDirectory())
        init(getOpencmsExtension())
        prepareDependentJars()
        createManifestFile()

    }


    void prepareDependentJars() {
        List<ModuleFile> jarfiles = new ArrayList<>()
        Configuration conf = project.getConfigurations().getByName('compile')
        conf.each { cf ->
            logger.debug("conf namr: " + cf)
        }
        project.getConfigurations().getByName('compile').each {
            println "jar file : " + it.name
            logger.debug("jar file : " + it.name)
            ModuleFile mf = new ModuleFile()
            mf.source = "system/modules/${getOpencmsExtension().moduleName}/lib/" + it.name
            mf.destination = "system/modules/${getOpencmsExtension().moduleName}/lib/" + it.name
            mf.type = 'binary'
            mf.dateCreated = new Date()
            mf.userCreated = new Date()
            jarfiles.add(mf)
        }

        if (getOpencmsExtension().buildJar) {
            // modules own jar file
            ModuleFile mfjar = new ModuleFile()
            mfjar.source = "system/modules/${getOpencmsExtension().moduleName}/lib/" + project.getRootProject().name + '.jar'
            mfjar.destination = mfjar.source
            mfjar.type = 'binary'
            mfjar.dateCreated = new Date()
            mfjar.userCreated = new Date()
            jarfiles.add(mfjar)
        }

        manifest.moduleFiles.files.addAll(jarfiles)
        debugModuleFiles()
    }


    def createManifestFile() {
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z")
        def root = "<export></export>"
        //StringWriter writer = new StringWriter()
        if (!manifestFile.exists()) {
            manifestFile.parentFile.mkdirs()
            manifestFile.createNewFile()
        }
        FileWriter writer = new FileWriter(manifestFile)
        def builder = new MarkupBuilder(writer)

        Info info1 = manifest.info
        Module module1 = manifest.module


        builder.export() {
            info() {
                opencms_version(info1.opencms_version)
                createdate(formatter.format(info1.createDate))
                infoproject({ getMkp().yieldUnescaped('<![CDATA[' + info1.info_project + ']]>') })
                export_version(info1.export_version)
            }

            module() {
                name(module1.name)
                nicename(module1.nicename)
                group(module1.group)
                'class'(module1.clazz)
                'import-script'(module1.importScript)
                'export-mode'(module1.exportMode)
                description({ getMkp().yieldUnescaped('<![CDATA[' + module1.description + ']]>') })
                version(module1.version)
                authorname({ getMkp().yieldUnescaped('<![CDATA[' + module1.authorname + ']]>') })
                authoremail({ getMkp().yieldUnescaped('<![CDATA[' + module1.authoremail + ']]>') })
                datecreated(formatter.format(module1.dateCreated))
                userinstalled(module1.userInstalled)
                dateinstalled(formatter.format(module1.dateInstalled))
                dependencies {
                    module1.dependencies.each { e ->
                        dependency(e.name)
                    }
                }
                exportpoints {
                    module1.exportPoints.each {
                        e ->
                            exportpoint() {
                                uri(e.uri)
                                destination(e.destination)
                            }
                    }
                }
                resources {
                    module1.resources.each {
                        e ->
                            ('resource'(uri: e.uri) {
                            })
                    }
                }
                excluderesources {
                    module1.excludeResources.each {
                        e ->
                            ('excluderesource'(uri: e.uri) {
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
                        datecreated(f.dateCreated)
                        flags(f.flags)
                        properties {
                            f.properties.each { prop ->
                                if (prop.type == PropertyType.SIMPLE) {
                                    property {
                                        name {
                                            prop.key
                                        }
                                        value({ getMkp().yieldUnescaped('<![CDATA[' + prop.value + ']]>') })
                                    }
                                } else {
                                    property(type: 'shared') {
                                        name {
                                            prop.key
                                        }
                                        value({ getMkp().yieldUnescaped('<![CDATA[' + prop.value + ']]>') })
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
    }
}
