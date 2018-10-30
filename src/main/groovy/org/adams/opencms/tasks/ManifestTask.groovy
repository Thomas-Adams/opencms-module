package org.adams.opencms.tasks

import groovy.transform.ToString
import groovy.xml.MarkupBuilder
import org.adams.opencms.beans.*
import org.adams.opencms.file.ModuleFileHandler
import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

import java.text.SimpleDateFormat

@ToString
class ManifestTask extends DefaultTask implements AccessExtension {

    Module module
    ModuleFileHandler moduleFileHandler = new ModuleFileHandler()
    List<ModuleFile> moduleFiles;

    File targetFile
    Manifest manifest = new Manifest()

    @Input
    Module getModule() {
        return module
    }

    void setModule(Module module) {
        this.module = module
        manifest.module = module
        manifest.info = module.info
    }

    @Input
    ModuleFileHandler getModuleFileHandler() {
        return moduleFileHandler
    }

    void setModuleFileHandler(ModuleFileHandler moduleFileHandler) {
        this.moduleFileHandler = moduleFileHandler
    }

    @Input
    File getTargetFile() {
        return targetFile
    }

    void setTargetFile(File targetFile) {
        this.targetFile = targetFile
    }

    List<ModuleFile> getModuleFiles() {
        return moduleFiles
    }

    void setModuleFiles(List<ModuleFile> moduleFiles) {
        this.moduleFiles = moduleFiles
    }

    @TaskAction
    def generateManifest() {
        moduleFileHandler.initModuleFiles()
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
            mf.source = 'system/modules/${opencms.name}/lib/' + it.name
            mf.destination = 'system/modules/${opencms.name}/lib/' + it.name
            mf.type = 'binary'
            mf.dateCreated = new Date()
            mf.userCreated = new Date()
            jarfiles.add(mf)
        }
        moduleFileHandler.moduleFiles.addAll(jarfiles)
        module.moduleFiles = moduleFileHandler.moduleFiles
        manifest.moduleFiles.setFiles(moduleFileHandler.moduleFiles)


    }


    def createManifestFile() {


        SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z")
        def root = "<export></export>"
        //StringWriter writer = new StringWriter()
        if (!targetFile.exists()) {
            targetFile.parentFile.mkdirs()
            targetFile.createNewFile()
        }
        println(targetFile.getAbsolutePath())
        println(targetFile.exists())
        println(targetFile.isDirectory())
        FileWriter writer = new FileWriter(targetFile)
        def builder = new MarkupBuilder(writer)

        Info info1 = manifest.info
        Module module1 = manifest.module
        List<ModuleFile> mfiles = moduleFileHandler.getModuleFiles()
        mfiles.addAll(this.jarDependencies)
        moduleFiles.files = mfiles;


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
                moduleFiles.files.each { f ->
                    file {
                        destination {
                            f.destination
                        }
                        type {
                            f.type
                        }
                        if (extension.createStructureUUID) {
                            uuidstructure {
                                f.uuidStructure
                            }
                        }
                        if (extension.createResourceUUID) {
                            uuidresource {
                                f.uuidResource
                            }
                        }
                        datecreated {
                            f.dateCreated
                        }
                        flags {
                            f.flags
                        }
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
        writer.flush()
    }
}
