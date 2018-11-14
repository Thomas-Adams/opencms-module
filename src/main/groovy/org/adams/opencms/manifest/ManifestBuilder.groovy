package org.adams.opencms.manifest

import groovy.xml.MarkupBuilder
import org.adams.opencms.beans.*
import org.adams.opencms.extension.OpenCmsExtension
import org.adams.opencms.file.ModuleFileHandler
import org.adams.opencms.parser.*

import java.text.SimpleDateFormat

class ManifestBuilder {


    def createManifestFile(File targetFile, File moduleDir, OpenCmsExtension extension) {
        Manifest manifest = new Manifest()
        manifest.info.creator = extension.userInstalled
        manifest.info.opencms_version = extension.opencCmsVersion
        manifest.info.info_project = extension.project
        manifest.info.export_version = extension.exportVersion
        manifest.module.version = extension.version
        manifest.module.userInstalled = extension.userInstalled
        manifest.module.dateCreated = extension.dateCreated
        manifest.module.importScript = extension.importScript
        manifest.module.group = extension.group
        manifest.module.excludeResources = extension.excludeResources
        manifest.module.name = extension.name
        manifest.module.clazz = extension.clazz
        manifest.module.resources = extension.resources
        manifest.module.exportPoints = extension.exportPoints
        manifest.module.dependencies = extension.dependencies
        manifest.module.dateInstalled = extension.dateInstalled
        manifest.module.authorname = extension.authorname
        manifest.module.authoremail = extension.authoremail
        manifest.module.description = extension.description
        manifest.module.exportMode = extension.exportMode
        manifest.module.nicename = extension.nicename


        SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z")
        ModuleFileHandler moduleFileHandler = new ModuleFileHandler()

        def root = "<export></export>"
        FileWriter writer = new FileWriter(targetFile)
        def builder = new MarkupBuilder(writer)
        builder.setDoubleQuotes(true)
        builder.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")
        Info info1 = manifest.info
        Module module1 = manifest.module

        DependencyXmlParser dependencyXmlParser = new DependencyXmlParser()
        List<Dependency> dependencyList = dependencyXmlParser.parseDependencies(new File(moduleDir.getAbsolutePath() + File.separator + 'dependencies.xml'))

        ExplorerTypeXmlParser explorerTypeXmlParser = new ExplorerTypeXmlParser()
        List<ExplorerType> explorerTypeList = explorerTypeXmlParser.parseDependencies(new File(moduleDir.getAbsolutePath() + File.separator + 'explorertypes.xml'))

        ExportPointXmlParser exportPointXmlParser = new ExportPointXmlParser()
        List<ExportPoint> exportPointList = exportPointXmlParser.parseDependencies(new File(moduleDir.getAbsolutePath() + File.separator + 'exportpoints.xml'))

        ParameterXmlParser parameterXmlParser = new ParameterXmlParser()
        List<Parameter> parameterList = parameterXmlParser.parseDependencies(new File(moduleDir.getAbsolutePath() + File.separator + 'parameters.xml'))

        ResourceXmlParser resourceXmlParser = new ResourceXmlParser()
        List<Resource> resourceList = resourceXmlParser.parseDependencies(new File(moduleDir.getAbsolutePath() + File.separator + 'resources.xml'))

        ResourceTypeXmlParser resourceTypeXmlParser = new ResourceTypeXmlParser()
        List<ResourceType> resourceTypeList = resourceTypeXmlParser.parseDependencies(new File(moduleDir.getAbsolutePath() + File.separator + 'resourcetypes.xml'))


        List<ModuleFile> mfiles = moduleFileHandler.getModuleFiles(moduleDir, extension)
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
                dependencies() {
                    dependencyList.each { e ->
                        dependency(name:e.name, version:e.version)
                    }
                }
                exportpoints() {
                    exportPointList.each {
                        e ->
                            exportpoint() {
                                uri(e.uri)
                                destination(e.destination)
                            }
                    }
                }
                resources() {
                    resourceList.each {
                        e ->
                            ('resource'(uri: e.uri) {
                            })
                    }
                }
                excluderesources() {
                    module1.excludeResources.each {
                        e ->
                            ('excluderesource'(uri: e.uri) {
                            })
                    }
                }
                parameters() {
                    parameterList.each {
                        e -> param(name: e.name, e.value)
                    }
                }

                resourcetypes() {
                    resourceTypeList.each { r ->
                        'type'('class': r.clazz, name: r.name, id: r.id) {
                            r.params.each { pa ->
                                param(name: pa.name, pa.value)
                            }
                            if (r.properties.size() > 0) {
                                properties() {
                                    r.properties.each { prop ->
                                        property() {
                                            name(prop.name)
                                            value(prop.value)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                explorertypes() {
                    explorerTypeList.each { e ->
                        explorertype(name: e.name, key: e.key, smalliconstyle: e.smalliconstyle, bigiconstyle: e.bigiconstyle, reference: e.reference) {
                            if (e.newResource) {
                                e.newResource.each { n ->
                                    newresource(page: n.page, uri: n.uri, order: n.order, autosetnavigation: n.autosetnavigation, autosettittle: n.autosettitle, info:
                                            n.info)
                                }
                            }
                            if (e.accessControl) {
                                e.accessControl.each { acc ->
                                    accesscontrol() {
                                        e.accessControl.accessEntries.each { ace ->
                                            acessentry(principal: ace.principal, permissions: ace.permissions)
                                        }
                                    }
                                }
                            }

                        }
                    }
                }
            }

            files {
                mfiles.each { f ->
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
