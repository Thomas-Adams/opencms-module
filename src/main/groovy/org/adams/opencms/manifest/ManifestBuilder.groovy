package org.adams.opencms.manifest

import groovy.xml.MarkupBuilder
import org.adams.opencms.beans.*
import org.adams.opencms.extension.OpenCmsExtension
import org.adams.opencms.file.ModuleFileHandler
import org.adams.opencms.tasks.AccessExtension
import org.gradle.api.Project

import java.text.SimpleDateFormat

class ManifestBuilder implements AccessExtension {


    private OpenCmsExtension extension

    ModuleFileHandler moduleFileHandler = new ModuleFileHandler()

    Project project

    File targetFile
    Manifest manifest

    void setProject(Project project) {
        this.project = project
    }
    List<ModuleFile> jarDependencies

    ManifestBuilder(List<ModuleFile> jarDependencies ) {
        this.project = getProject()
        this.setOpenCmsExtension(getOpencmsExtension())
        this.jarDependencies = jarDependencies
    }

    void setOpenCmsExtension(OpenCmsExtension extension) {
        this.extension = extension

        manifest.info.creator = this.extension.userInstalled
        manifest.info.opencms_version = this.extension.opencCmsVersion
        manifest.info.info_project = this.extension.project
        manifest.info.export_version = this.extension.exportVersion
        manifest.module.version = this.extension.version
        manifest.module.userInstalled = this.extension.userInstalled
        manifest.module.dateCreated = this.extension.dateCreated
        manifest.module.importScript = this.extension.importScript
        manifest.module.group = this.extension.group
        manifest.module.parameters = this.extension.parameters
        manifest.module.excludeResources = this.extension.excludeResources
        manifest.module.name = this.extension.name
        manifest.module.clazz = this.extension.clazz
        manifest.module.resources = this.extension.resources
        manifest.module.exportPoints = this.extension.exportPoints
        manifest.module.dependencies = this.extension.dependencies
        manifest.module.dateInstalled = this.extension.dateInstalled
        manifest.module.authorname = this.extension.authorname
        manifest.module.authoremail = this.extension.authoremail
        manifest.module.description = this.extension.description
        manifest.module.exportMode = this.extension.exportMode
        manifest.module.nicename = this.extension.nicename
        this.moduleFileHandler.moduleDir = project.file(this.extension.moduleDir)
        this.targetFile = project(this.extension.manifestTargetFile)
    }


    private void validateTaskProperties() {
        if(!manifest.module.name())
            throw new org.gradle.api.InvalidUserDataException('The "moduleName" must be set in order to generate manifest')
        if(!manifest.module.version)
            throw new org.gradle.api.InvalidUserDataException('The "moduleVersion" must be set in order to generate manifest')
        if(!manifest.module.authorname)
            throw new org.gradle.api.InvalidUserDataException('The "moduleAuthor" must be set in order to generate manifest')
        if(!manifest.module.exportPoints)
            throw new org.gradle.api.InvalidUserDataException('The "export points" must be set in order to generate manifest')
        if(!moduleFileHandler.moduleDir)
            throw new org.gradle.api.InvalidUserDataException('The "moduleVfsDir" must be set in order to generate manifest')
        if(!targetFile || !targetFile.exists())
            throw new org.gradle.api.InvalidUserDataException("The \"moduleVfsDir\" target file (${targetFile.path}) must exist in order to generate manifest")
        if(!manifest.info.opencms_version)
            throw new org.gradle.api.InvalidUserDataException('The "opencmsVersion" must be set in order to generate manifest')
    }

    def createManifestFile() {


        validateTaskProperties()

        SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z")
        def root = "<export></export>"
        //StringWriter writer = new StringWriter()
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
                        if(extension.createStructureUUID) {
                            uuidstructure {
                                f.uuidStructure
                            }
                        }
                        if(extension.createResourceUUID) {
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
                                        value({getMkp().yieldUnescaped('<![CDATA[' +prop.value + ']]>')})
                                    }
                                } else {
                                    property(type: 'shared') {
                                        name {
                                            prop.key
                                        }
                                        value({getMkp().yieldUnescaped('<![CDATA[' +prop.value + ']]>')})
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
