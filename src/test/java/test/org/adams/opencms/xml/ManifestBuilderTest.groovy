package test.org.adams.opencms.xml

import groovy.xml.MarkupBuilder
import org.adams.opencms.beans.*

import java.text.SimpleDateFormat

class ManifestBuilderTest extends GroovyTestCase {


    void testXmlRootAlt1() {
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z")
        def root = "<export></export>"
        StringWriter writer = new StringWriter()
        def builder = new MarkupBuilder(writer)
        builder.expandEmptyElements = false
        builder.doubleQuotes = true
        builder.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")
        Info info1 = new Info()
        info1.createDate = new Date()
        info1.creator = 'Admin'
        info1.opencms_version = '10.5.4'
        info1.export_version = '10.0'
        info1.info_project = 'Offline'


        Module module1 = new Module()
        module1.description = 'Opencms Sync Tool'
        module1.exportMode = 'default'
        module1.nicename = ' Sync Tool'
        module1.name = 'org.adams.opencms.synchron.module'
        module1.version = '1.0.0'
        module1.authoremail = 'heinrich.adams@gmaul.com'
        module1.authorname = 'tadams'
        module1.clazz = null
        module1.dateCreated = new Date()
        module1.dateInstalled = new Date()
        module1.dependencies = []

        ExportPoint classes = new ExportPoint()
        classes.destination = 'WEB-INF/classes/'
        classes.uri = '/system/modules/org.adams.opencms.synchron.module/classes/'
        ExportPoint libs = new ExportPoint()
        libs.destination = 'WEB-INF/lib/'
        libs.uri = '/system/modules/org.adams.opencms.synchron.module/lib/'
        module1.exportPoints.add(classes)
        module1.exportPoints.add(classes)
        Resource resource1 = new Resource()
        resource1.uri = '/system/modules/org.adams.opencms.synchron.module/'
        module1.resources.add(resource1)
        Parameter p1 = new Parameter()
        p1.name = 'args'
        p1.value = 20
        module1.parameters.add(p1)


        ModuleFiles moduleFiles = new ModuleFiles()
        moduleFiles.files


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

            files() {
                moduleFiles.files.each { f ->

                }
            }


        }
        println(writer.toString())
    }
}