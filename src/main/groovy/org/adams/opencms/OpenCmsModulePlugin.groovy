package org.adams.opencms


import org.adams.opencms.tasks.MyTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.Copy

class OpenCmsModulePlugin implements Plugin<Project> {

    static final String GROUP_NAME = 'OpenCms'
    static final String EXT_NAME = 'opencms'
    static final String MODULE_TASK = 'module'
    static final String MANIFEST_COPY_FILES = 'copyModulesFiles'
    static final String MANIFEST_PREPARE_TASK = 'moduleManifestPreparation'
    static final String MANIFEST_COPY_JAR = 'copyJarFile'
    static final String MANIFEST_COPY_DEPENDENT_JARS = 'copyDependentJarFiles'
    static final String DEFAULT_VFS_PATH = 'src/main/vfs'


    @Override
    void apply(Project project) {


        project.apply(plugin: 'base')
        def opencms = project.extensions.create(EXT_NAME, org.adams.opencms.extension.OpenCmsExtension)



        project.task(MANIFEST_COPY_FILES,
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

        project.task(MANIFEST_COPY_JAR,
                type: Copy,
                group: GROUP_NAME,
                description: 'copies the jar file to modules build directory.') {
            project.afterEvaluate { p ->
                dependsOn = ['jar', 'war', MANIFEST_COPY_FILES]

                from (p.file("build/libs")) {
                    include '**/*.jar'
                }
                into (p.file("build/${opencms.moduleName}_${opencms.moduleVersion}/system/modules/${opencms.moduleName}/lib"))
            }
        }
        project.task(MANIFEST_COPY_DEPENDENT_JARS,
                type: Copy,
                group: GROUP_NAME,
                description: 'copies the depemndent jar files to modules build directory.') {
            project.afterEvaluate { p ->
                dependsOn = ['jar', MANIFEST_COPY_FILES, MANIFEST_COPY_JAR]

                Configuration conf = project.getConfigurations().getByName('compile')
                conf.each { cf ->
                    logger.debug("conf namr: " + cf)
                    from cf
                    into (p.file("build/${opencms.moduleName}_${opencms.moduleVersion}/system/modules/${opencms.moduleName}/lib"))
                }

            }
        }
        project.task(
                MANIFEST_PREPARE_TASK,
                type: MyTask,
                group: GROUP_NAME,
                description: 'Generates the module manifest.xml file.') {
            project.afterEvaluate { p ->
                dependsOn = ['jar', MANIFEST_COPY_FILES, MANIFEST_COPY_FILES]
                println("Module name: " + opencms.moduleName)
                moduleDir = project.file("build/${opencms.moduleName}_${opencms.moduleVersion}")
                manifestFile = new File(moduleDir.getAbsolutePath() + "/manifest.xml")
                manifestFile.createNewFile()
                sourceDir = project.file(opencms.moduleDir)

            }
        }
    }

}
