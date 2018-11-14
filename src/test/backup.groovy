package org.adams.opencms

import org.adams.opencms.tasks.CreateMissingMetaFilesTask
import org.adams.opencms.tasks.CustomTask
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
    static final String MODULE_MANIFEST_TASK = 'moduleManifestPreparation'
    static final String MODILE_COPY_JAR = 'copyJarFile'
    static final String MODULE_ZIP_MODULE = 'zipModule'
    static final String MODULE_COPY_DEPENDENT_JARS = 'copyDependentJarFiles'
    static final String MODULE_LIST_DEPENDENT_JARS = 'listDependentJarFiles'
    static final String MODULE_CREATE_MISSING_META = 'createMissingMetaFiles'
    static final String MODULE_CUSTOM_TASK = 'customTask'
    static final String DEFAULT_VFS_PATH = 'src/main/vfs'


    @Override
    void apply(Project project) {


        project.apply(plugin: 'base')
        def opencms = project.extensions.create(EXT_NAME, org.adams.opencms.extension.OpenCmsExtension)


        project.task(MODULE_CUSTOM_TASK,
                type: CustomTask,
                group: GROUP_NAME,
                description: 'Create missing _meta.json files') {
            project.afterEvaluate { p ->
                dependsOn = []
            }
        }

        project.task(MODULE_CREATE_MISSING_META,
                type: CreateMissingMetaFilesTask,
                group: GROUP_NAME,
                description: 'Create missing _meta.json files') {
            project.afterEvaluate { p ->
                dependsOn = []
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
        project.task(MODULE_COPY_FILES,
                type: Copy,
                group: GROUP_NAME,
                description: 'copies module file to modules build directory.'
        ) {
            project.afterEvaluate { p ->
                (p.file("build/${opencms.moduleName}_${opencms.moduleVersion}")).mkdirs()
                dependsOn = ['jar', 'war']

                into(p.file("build/${opencms.moduleName}_${opencms.moduleVersion}"))
                from(project.file(p.opencms.moduleDir)) {
                    exclude '**/*_meta.json', '**/ignore.txt', '**/.git/*', '**/.svn/*', '**/CVS/*', '**/.cvsignore', '**/.nbattrs', '**/.project', '**/.classpath',
                            '**/module.properties', '**/dependencies.xml', '**/resources.xml', '**/resourcetypes.xml', '**/explorertypes.xml', '**/relations' +
                            '.xml', '**/parameters.xml', '**/exportpoints.xml'
                }
            }
        }

        project.task(MODILE_COPY_JAR,
                type: Copy,
                group: GROUP_NAME,
                description: 'copies the jar file to modules build directory.') {
            project.afterEvaluate { p ->
                dependsOn = ['jar', 'war', MODULE_COPY_FILES]

                from(p.file("build/libs")) {
                    include '**/*.jar'
                }
                into(p.file("build/${opencms.moduleName}_${opencms.moduleVersion}/system/modules/${opencms.moduleName}/lib"))
            }
        }
        project.task(MODULE_COPY_DEPENDENT_JARS,
                type: Copy,
                group: GROUP_NAME,
                description: 'copies the dependent jar files to modules build directory.') {
            project.afterEvaluate { p ->
                dependsOn = ['jar', MODULE_COPY_FILES, MODILE_COPY_JAR]

                Configuration conf = project.getConfigurations().getByName('compile')
                conf.each { cf ->
                    logger.debug("conf name: " + cf)
                    from cf
                    into(p.file("build/${opencms.moduleName}_${opencms.moduleVersion}/system/modules/${opencms.moduleName}/lib"))
                }

            }
        }
        project.task(
                MODULE_MANIFEST_TASK,
                type: DefaultTask,
                group: GROUP_NAME,
                description: 'Generates the module manifest.xml file.') {
            project.afterEvaluate { p ->
                dependsOn = ['jar', MODILE_COPY_JAR, MODULE_COPY_FILES, MODULE_COPY_DEPENDENT_JARS]
                println("Module name: " + opencms.moduleName)
                moduleDir = project.file("build/${opencms.moduleName}_${opencms.moduleVersion}")
                manifestFile = new File(moduleDir.getAbsolutePath() + "/manifest.xml")
                manifestFile.createNewFile()
                sourceDir = project.file(opencms.moduleDir)

            }
        }

        project.task(
                MODULE_ZIP_MODULE,
                type: Zip,
                group: GROUP_NAME,
                description: 'Zips the modfule files.') {
            project.afterEvaluate { p ->
                dependsOn = ['jar', MODILE_COPY_JAR, MODULE_COPY_FILES, MODULE_COPY_DEPENDENT_JARS, MODULE_MANIFEST_TASK]
                archiveName = "${opencms.moduleName}_${opencms.moduleVersion}.zip"
                def moduleZipDir = p.file("build/${p.opencms.moduleName}_${p.opencms.moduleVersion}")
                // default value via java plugin: destinationDir = project.distsDir aka build/distributions
                inputs.dir moduleZipDir
                outputs.file "${destinationDir}/${archiveName}"

                from(moduleZipDir)
                includeEmptyDirs = true
            }
        }
    }
}

