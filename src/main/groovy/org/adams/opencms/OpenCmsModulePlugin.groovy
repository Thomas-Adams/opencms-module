package org.adams.opencms

import org.adams.opencms.extension.OpenCmsExtension
import org.adams.opencms.tasks.*
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.bundling.Zip

class OpenCmsModulePlugin implements Plugin<Project> {

    static final String GROUP_NAME = 'OpenCms'
    static final String EXT_NAME = 'opencms'
    static final String MODULE_COPY_FILES = 'copyModulesFiles'
    static final String MODULE_MANIFEST_TASK = 'createManifest'
    static final String MODULE_ZIP_MODULE = 'zipModule'
    static final String MODULE_LIST_DEPENDENT_JARS = 'listDependentJarFiles'
    static final String MODULE_CREATE_MISSING_META = 'createMissingMetaFiles'


    static final String MODULE_CREATE_META_FROM_MANIFEST = 'createMetaFromManifest'
    static final String MODULE_CREATE_DEPENDENCIES_FROM_MANIFEST = 'createDependenciesFromManifest'
    static final String MODULE_CREATE_EXPLORERTYPES_FROM_MANIFEST = 'createExplorerTypesFromManifest'
    static final String MODULE_CREATE_EXPORTPOINTS_FROM_MANIFEST = 'createExportPointsFromManifest'
    static final String MODULE_CREATE_PARAMETERS_FROM_MANIFEST = 'createParametersFromManifest'
    static final String MODULE_CREATE_RESOURCES_FROM_MANIFEST = 'createResourcesFromManifest'
    static final String MODULE_CREATE_RESOURCETYPES_FROM_MANIFEST = 'createResourceTypesFromManifest'
    static final String MODULE_COLLECT_DEPENDENT_JARFILES = 'collectDependentJarFiles'
    static final String MODULE_CUSTOM_TASK = 'customTask'


    @Override
    void apply(Project project) {

        project.apply(plugin: 'base')
        def opencms = project.extensions.create(EXT_NAME, OpenCmsExtension)

        project.task(MODULE_CUSTOM_TASK,
                type: CustomTask,
                group: GROUP_NAME,
                description: 'Create missing _meta.json files') {
            project.afterEvaluate { p ->
                dependsOn = []
            }
        }


        project.task(MODULE_CUSTOM_TASK,
                type: ManifestTask,
                group: GROUP_NAME,
                description: 'Create manifest file in build directory') {
            project.afterEvaluate { p ->
                dependsOn = ['jar', MODULE_COLLECT_DEPENDENT_JARFILES, MODULE_CREATE_MISSING_META, MODULE_COPY_FILES]
            }
        }

        project.task(
                MODULE_ZIP_MODULE,
                type: Zip,
                group: GROUP_NAME,
                description: 'Zips the modfule files.') {
            project.afterEvaluate { p ->
                dependsOn = ['jar', MODULE_COPY_FILES, MODULE_MANIFEST_TASK]
                archiveName = "${opencms.moduleName}_${opencms.moduleVersion}.zip"
                def moduleZipDir = p.file("build/${p.opencms.moduleName}_${p.opencms.moduleVersion}")
                // default value via java plugin: destinationDir = project.distsDir aka build/distributions
                inputs.dir moduleZipDir
                outputs.file "${destinationDir}/${archiveName}"

                from(moduleZipDir)
                includeEmptyDirs = true
            }
        }


        project.task(MODULE_COLLECT_DEPENDENT_JARFILES,
                type: Copy,
                group: GROUP_NAME,
                description: 'Collects the dependent jar files and copies them into the "/system/modules/<moduleName>/lib" folder') {
            project.afterEvaluate { p ->
                dependsOn = []
                Configuration conf = project.getConfigurations().getByName('compile')
                conf.each { cf ->
                    logger.debug("conf name: " + cf)
                    from cf
                    into(p.file("${opencms.moduleDir}/system/modules/${opencms.moduleName}/lib"))
                }
            }
        }

        project.task(MODULE_COPY_FILES,
                type: Copy,
                group: GROUP_NAME,
                description: 'copies module file to modules build directory.'
        ) {
            project.afterEvaluate { p ->
                (p.file("build/${opencms.moduleName}_${opencms.moduleVersion}")).mkdirs()
                dependsOn = ['jar', MODULE_COLLECT_DEPENDENT_JARFILES, MODULE_CREATE_MISSING_META]

                into(p.file("build/${opencms.moduleName}_${opencms.moduleVersion}"))
                from(project.file(p.opencms.moduleDir)) {
                    exclude '**/*_meta.json', '**/ignore.txt', '**/.git/*', '**/.svn/*', '**/CVS/*', '**/.cvsignore', '**/.nbattrs', '**/.project', '**/.classpath',
                            '**/module.properties', '**/dependencies.xml', '**/resources.xml', '**/resourcetypes.xml', '**/explorertypes.xml', '**/relations' +
                            '.xml', '**/parameters.xml', '**/exportpoints.xml'
                }
            }
        }

        project.task(MODULE_LIST_DEPENDENT_JARS,
                type: DefaultTask,
                group: GROUP_NAME,
                description: 'Lists all the dependent jar files of the module java code') {
            project.afterEvaluate { p ->
                dependsOn = []
                Configuration conf = project.getConfigurations().getByName('compile')
                conf.each { cf ->
                    println(cf)
                }
            }
        }

        project.task(MODULE_CREATE_META_FROM_MANIFEST,
                type: CreateMetaFilesFromManifestTask,
                group: GROUP_NAME,
                description: 'Create the meta files from the initial manifest.xml'
        ) {
            project.afterEvaluate { p ->
                dependsOn = []
            }
        }

        project.task(MODULE_CREATE_DEPENDENCIES_FROM_MANIFEST,
                type: CreateDependenciesFromManifestTask,
                group: GROUP_NAME,
                description: 'Create the dependencies.xml from the initial manifest.xml'
        ) {
            project.afterEvaluate { p ->
                dependsOn = []
            }
        }

        project.task(MODULE_CREATE_EXPLORERTYPES_FROM_MANIFEST,
                type: CreateExplorerTypesFromManifestTask,
                group: GROUP_NAME,
                description: 'Create the explorertypes.xml from the initial manifest.xml'
        ) {
            project.afterEvaluate { p ->
                dependsOn = []
            }
        }

        project.task(MODULE_CREATE_EXPORTPOINTS_FROM_MANIFEST,
                type: CreateExportPointsFromManifestTask,
                group: GROUP_NAME,
                description: 'Create the exportpoints.xml from the initial manifest.xml'
        ) {
            project.afterEvaluate { p ->
                dependsOn = []
            }
        }

        project.task(MODULE_CREATE_PARAMETERS_FROM_MANIFEST,
                type: CreateParametersFromManifestTask,
                group: GROUP_NAME,
                description: 'Create the parameters.xml from the initial manifest.xml'
        ) {
            project.afterEvaluate { p ->
                dependsOn = []
            }
        }

        project.task(MODULE_CREATE_RESOURCES_FROM_MANIFEST,
                type: CreateResourcesFromManifestTask,
                group: GROUP_NAME,
                description: 'Create the resources.xml from the initial manifest.xml'
        ) {
            project.afterEvaluate { p ->
                dependsOn = []
            }
        }

        project.task(MODULE_CREATE_RESOURCETYPES_FROM_MANIFEST,
                type: CreateResourceTypesFromManifestTask,
                group: GROUP_NAME,
                description: 'Create the resourcetypes.xml from the initial manifest.xml'
        ) {
            project.afterEvaluate { p ->
                dependsOn = []
            }
        }

        project.task(MODULE_CREATE_MISSING_META,
                type: CreateMissingMetaFilesTask,
                group: GROUP_NAME,
                description: 'Create the missing meta files based on the file info'
        ) {
            project.afterEvaluate { p ->
                dependsOn = [MODULE_COLLECT_DEPENDENT_JARFILES]
            }
        }
    }
}

