package org.adams.opencms.manifest

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import groovy.json.JsonOutput
import org.adams.opencms.beans.*
import org.adams.opencms.extension.OpenCmsExtension
import org.adams.opencms.tasks.AccessExtension
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction

import java.text.SimpleDateFormat

class ManifestParser implements AccessExtension {

    private static final String DATE_FORMAT = 'EEE, dd MMM yyyy HH:mm:ss z'

    File manifestFile


    ManifestParser(OpenCmsExtension extension) {
        this.project = null
        this.extension = extension
    }

    ManifestParser(Project project, OpenCmsExtension extension) {
        this.project = project
        this.extension = extension

    }

    def File initParser(Project project, OpenCmsExtension extension) {
        return project.file(extension.moduleDir).getAbsolutePath() + 'manifest.xml'
    }

    Manifest parseExistingManifestFile(File manifestFile) {
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT)

        Manifest manifest = new Manifest()


        String fileContents = manifestFile.getText('UTF-8')
        def export = new XmlSlurper().parseText(fileContents)

        if (export.info) {
            manifest.info.info_project = export.info.infoproject ? export.info.infoproject : null
            manifest.info.export_version = export.info.export_version ? export.info.export_version : null
            manifest.info.creator = export.info.creator ? export.info.creator : null
            manifest.info.opencms_version = export.info.opencms_version ? export.info.opencms_version : null
            manifest.info.createDate = export.info.createdate && export.info.createdate != '' ? formatter.parse(export.info.createdate.text()) : null
        }

        if (export.module) {
            manifest.module.name = export.module.name ? export.module.name : null
            manifest.module.nicename = export.module.nicename ? export.module.nicename : null
            manifest.module.group = export.module.group ? export.module.group : null
            manifest.module.exportMode = export.module.'export-mode' ? export.module.'export-mode'['@name'] : null
            manifest.module.clazz = export.module.'class' ? export.module.'class' : null
            manifest.module.description = export.module.description ? export.module.description.text() : null
            manifest.module.authorname = export.module.authorname ? export.module.authorname.text() : null
            manifest.module.authoremail = export.module.authoremail ? export.module.authoremail.text() : null
            manifest.module.version = export.module.version ? export.module.version.text() : null
            manifest.module.dateCreated = export.module.datecreated && export.module.datecreated != '' ? formatter.parse(export.module.datecreated.text()) : null
            manifest.module.userInstalled = export.module.userInstalled ? export.module.userInstalled.text() : null
            manifest.module.dateInstalled = export.module.dateInstalled && export.module.dateInstalled != '' ? formatter.parse(export.module.dateInstalled.text()) : null
            manifest.module.dependencies = null
            if (export.module.dependencies) {
                if (export.module.dependencies.dependency.collect().size() > 0) {
                    List<Dependency> dependencies = new ArrayList<>()
                    export.module.dependencies.dependency.findAll().each { it ->
                        Dependency dep = new Dependency()
                        dep.name = it['@name']
                        dep.version = it['@version']
                        dependencies.add(dep)
                    }
                    manifest.module.dependencies = dependencies
                }
            }

            if (export.module.exportpoints) {
                if (export.module.exportpoints.exportpoint.collect().size() > 0) {
                    List<ExportPoint> exportPoints = new ArrayList<>()
                    export.module.exportpoints.exportpoint.findAll().each { it ->
                        ExportPoint exportPoint = new ExportPoint()
                        exportPoint.uri = it['@uri']
                        exportPoint.destination = it['@destination']
                        exportPoints.add(exportPoint)
                    }
                    manifest.module.exportPoints = exportPoints
                }
            }

            if (export.module.resources) {
                if (export.module.resources.resource.collect().size() > 0) {
                    List<Resource> resources = new ArrayList<>()
                    export.module.resources.resource.findAll().each { it ->
                        Resource resource = new Resource()
                        resource.uri = it['@uri']
                        resources.add(resource)
                    }
                    manifest.module.resources = resources
                }
            }
            if (export.module.excluderesources) {
                if (export.module.excluderesources.excluderesource.collect().size() > 0) {
                    List<Resource> resources = new ArrayList<>()
                    export.module.excluderesources.excluderesource.findAll().each { it ->
                        Resource resource = new Resource()
                        resource.uri = it['@uri']
                        resources.add(resource)
                    }
                    manifest.module.excludeResources = resources
                }
            }

            if (export.module.paramaters) {
                if (export.module.paramaters.parameter.collect().size() > 0) {
                    List<Parameter> parameters = new ArrayList<>()
                    export.module.paramaters.parameter.findAll().each { it ->
                        Parameter param = new Parameter()
                        param.name = it['@name']
                        param.value = it['@value']
                        parameters.add(param)
                    }
                    manifest.module.parameters = parameters
                }
            }
        }

        if (export.files) {
            List<ModuleFile> moduleFiles = new ArrayList<>()
            if (export.files.file.collect().size() > 0) {
                export.files.file.findAll().each { it ->

                    ModuleFile mf = new ModuleFile()

                    mf.source = it.source ? it.source.text() : null
                    mf.destination = it.destination ? it.destination : null
                    mf.type = it.type ? it.type : null
                    if (extension.createStructureUUID) {
                        mf.uuidStructure = it.uuidstructure ? it.uuidstructure : null
                    } else {
                        mf.uuidStructure = null
                    }
                    if (extension.createResourceUUID) {
                        mf.uuidResource = it.uuidresource ? it.uuidresource : null
                    } else {
                        mf.uuidResource = null
                    }
                    mf.dateCreated = it.datecreated && it.datecreated != '' ? formatter.parse(it.datecreated.text()) : null
                    mf.flags = it.flags && it.flags != '' ? Integer.parseInt(it.flags.text()) : null
                    if (it.properties) {

                        if (it.properties.property.collect().asList().size() > 0) {
                            List<Property> properties = new ArrayList<>()
                            it.properties.property.findAll().each { prop ->
                                PropertyType propertyType = prop['@type'] && prop['@type'].text() == 'shared' ? PropertyType.SHARED : PropertyType.SIMPLE
                                Property p = new Property(propertyType, prop.name.text(), prop.value.text());
                                properties.add(p)
                            }
                            mf.properties = properties
                        }

                    }
                    if (it.relations) {
                        if (it.relations.relation.collect().asList().size() > 0) {
                            List<Relation> relations = new ArrayList<>()
                            it.relations.relation.findAll().each { rel ->
                                Relation relation = new Relation()
                                relation.element = it['@element']
                                relation.type = it['@type'] && it['@type'].text() == 'WEAK' ? RelationTypes.WEAK : RelationTypes.STRONG
                                relation.invalidate = it['@invalidate']
                            }
                            mf.relations = relations
                        }

                    }
                    if (it.accesscontrol) {

                        if (it.accesscontrol.accessentry.collect().asList().size() > 0) {
                            List<AccessEntry> accessEntries = new ArrayList<>()
                            it.accesscontrol.accessentry.findAll().each { ace ->
                                AccessEntry accessEntry = new AccessEntry()
                                accessEntry.flags = ace.flags ? Integer.parseInt(ace.flags) : 0
                                accessEntry.principal = ace.uuidprincipal ? ace.uuidprincipal : null
                                if (it.permissionset) {
                                    accessEntry.permissionSet.allowed = ace.permissionset && ace.permissionset.allowed ? ace.permissionset.allowed : accessEntry.permissionSet.allowed
                                    accessEntry.permissionSet.denied = ace.permissionset && ace.permissionset.denied ? ace.permissionset.denied : accessEntry.permissionSet.denied
                                } else {
                                    accessEntry.permissionSet = null
                                }
                                accessEntries.add(accessEntry)
                            }
                            mf.accessControl.accessEntries = accessEntries
                        }
                    }
                    moduleFiles.add(mf)
                }

            }

            ModuleFiles mfs = new ModuleFiles()
            mfs.files = moduleFiles
            manifest.moduleFiles = mfs
        }
        return manifest
    }

    @TaskAction
    def createMetaFiles() {
        ObjectMapper objectMapper = new ObjectMapper()
        objectMapper.setDateFormat(new SimpleDateFormat(DATE_FORMAT))
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_ABSENT);
        manifestFile = initParser(getProject(), getOpencmsExtension())
        Manifest manifest = parseExistingManifestFile(manifestFile)
        moduleDir = extension.moduleVfsDirectory
        moduleDir = moduleDir.replace('\\', '/')
        if (!moduleDir.endsWith("/")) {
            moduleDir += "/"
        }

        manifest.moduleFiles.files.each { it ->
            String metaName = ''
            String f = ''
            if (it.source && it.source != '') {
                f = moduleDir + (it.source.startsWith('/') ? it.source.substring(1) : it.source)
            } else {
                f = moduleDir + (it.destination.startsWith('/') ? it.destination.substring(1) : it.destination)
            }

            if (it.type.equals('folder')) {
                File file = new File(f)
                file.mkdirs()
                metaName = file.getAbsolutePath() + File.separator + '_meta.json';
            } else {
                File file = new File(f)
                File parent = file.parentFile
                parent.mkdirs()
                metaName = file.getAbsolutePath() + '_meta.json'
            }
            if (!extension.createResourceUUID) {
                it.uuidResource = null
            }
            if (!extension.createStructureUUID) {
                it.uuidStructure = null
            }

            File metaFile = new File(metaName)
            FileWriter fileWriter = new FileWriter(metaFile);
            String json = objectMapper.writeValueAsString(it)
            fileWriter.write(JsonOutput.prettyPrint(json))
            fileWriter.flush()
            fileWriter.close()

        }


    }

}