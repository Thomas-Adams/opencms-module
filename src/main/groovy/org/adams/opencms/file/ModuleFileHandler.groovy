package org.adams.opencms.file

import groovy.json.JsonSlurper
import org.adams.opencms.beans.*
import org.adams.opencms.extension.OpenCmsExtension
import org.adams.opencms.utils.ModuleFileFactory
import org.apache.commons.io.FilenameUtils
import org.gradle.api.InvalidUserDataException

import java.text.SimpleDateFormat

/**
 * This class parses throw the whole vfs folder recursively to build up the
 * module's manifest.xml file.
 *
 * OpenCms' VFS allows for setting properties on the files and folders.
 * In order to mimic this you can provide meta files which the manifest task then
 * read and inserts into the resulting manifest.xml, OpenCms' will then set the
 * properties of folders when the module is imported.
 *
 * To set properties for a folder you place the meta inside folder like this:
 * <code>
 *     .../vfs/system/com.some.module/resources/css/
 *     .../vfs/system/com.some.module/resources/css/_meta.json
 * </code>
 *
 * In case of files add _meta.json as extension to the original filename:
 * <code>
 *      .../vfs/system/com.some.module/formatters/employee.jsp
 *      .../vfs/system/com.some.module/formatters/employee.jsp_meta.json
 * </code>
 *
 *{*     "uuidstructure": ....,
 *     .....
 *     "type": "jsp",
 *     "sharedProperties": { "export": false },
 *     "properties": {*         "cache": "container-element"
 *},
 *      "relations": [...]
 *      "accesscontrol" : [ ... ]
 *}*
 * Regarding permissions and access control there are two different approaches :
 *
 * 1. Approach
 * "accesscontrol" : [
 *
 *{*  				"uuidprincipal" : "ROLE.WORKPLACE_USER",
 * 					"flags" : 514,
 * 					"permissionSet:
 *{ 	"allowed" : 1
 * 							"denied" : 0
 *}*}* ]
 * 2. Approach
 * 	"accesscontrol" :
 * 	    [
 *{* 					"principal" : "ROLE.WORKPLACE_USER",
 * 					permissions: "+r+v+w+c"
 *}, ...
 * 	    ]
 *
 * During the parsing of all _meta.json files both approaches are taken into account, however the first approach is
 * the one which comes with the export of the module.
 */
class ModuleFileHandler {

    SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z")

    ModuleFile getModuleFileForJar(File moduleDir, OpenCmsExtension extension) {
        return ModuleFileFactory.forJarFile(moduleDir, extension)
    }

    List<ModuleFile> getModuleFiles(File moduleDir, OpenCmsExtension extension) {

        List<ModuleFile> moduleFiles = new ArrayList<ModuleFile>();
        moduleDir.eachFileRecurse {
            if (!ModuleFileFactory.isMetaFile(it)) {
                ModuleFile mf = new ModuleFile()
                String fileName = it.getAbsolutePath().minus(moduleDir.getAbsolutePath())
                if (fileName.startsWith('/') || fileName.startsWith('\\')) {
                    fileName = fileName.substring(1)
                }

                fileName = FilenameUtils.separatorsToUnix(fileName)
                println('Module filename ' + fileName)
                mf.setDestination(fileName)
                //TODO: distinguish _meta.json for file and folder
                File metaFile = new File(it.getAbsolutePath() + '_meta.json')
                if (meta.exists()) {
                    metaInfo = new JsonSlurper().parse(metaFile, 'UTF-8')
                    if (metaInfo.source) {
                        mf.setSource(metaInfo.source)
                    }
                    if (it.isFile()) {
                        if (!metaInfo.source) {
                            mf.source = fileName
                        }
                    }
                    if (metaInfo.datelastmodified) {
                        mf.dateLastModified = formatter.parse(metaInfo.datelastmodified)
                    } else {
                        mf.dateLastModified = new Date()
                    }

                    if (metaInfo.datecreated) {
                        mf.dateCreated = formatter.parse(metaInfo.datecreated)
                    } else {
                        mf.dateCreated = new Date()
                    }

                    if (metaInfo.userlastmodified) {
                        mf.userLastModified = metaInfo.userlastmodified
                    } else {
                        mf.userLastModified = extension.user
                    }

                    if (metaInfo.usercreated) {
                        mf.userCreated = metaInfo.usercreated
                    } else {
                        mf.userCreated = extension.user
                    }
                    if (metaInfo.flags) {
                        mf.flags = Integer.parseInt(metaInfo.flags + '')
                    } else {
                        mf.flags = 0
                    }
                    if (metaInfo.type) {
                        mf.setType(meta.type)
                    } else {
                        mf.setType(ModuleFileFactory.guessFileType(it.getAbsolutePath()))
                    }
                    if (metaInfo.properties) {
                        metaInfo.properties.each {
                            mf.properties.add(new Property(PropertyType.SIMPLE, it.key.toString(), it.value.toString()))
                        }
                    }
                    if (metaInfo.sharedProperties) {
                        metaInfo.sharedProperties.each {
                            mf.properties.add(new Property(PropertyType.SHARED, it.key.toString(), it.value.toString()))
                        }
                    }
                    if (metaInfo.uuidstructure) {
                        mf.uuidStructure = metaInfo.uuidstructure
                    }
                    if (metaInfo.uuidresource) {
                        mf.uuidResource = metaInfo.uuidresource
                    }
                    handleAccessControl(metaInfo, mf)
                    handleRelation(metaInfo, mf)
                } else {
                    if (!extension.createMetaInfoOnFly) {
                        throw new InvalidUserDataException('Meta file missing, you need to create the _meta.json file for ${filename} first or activate the ' +
                                '"createMetaInfoOnFly" flag (set it to true)')
                    } else {
                        CreateMissingMetaFiles cm = new CreateMissingMetaFiles()
                        cm.createMissingMetaFileFromFile(moduleDir, it)
                        mf = ModuleFileFactory.fromFile(it, moduleDir)
                    }
                }
                moduleFiles.add(mf)
            }
        }
        moduleFiles.add(getModuleFileForJar(moduleDir, extension))
        return moduleFiles
    }


    def handleAccessControl(metaInfo, ModuleFile mf) {
        if (isCollectionOrArray(metaInfo.accessControl)) {
            if (metaInfo.accessControl[0].principal) {
                handleAccessControlSecondApproach(metaInfo)
            } else {
                handleAccessControlFirstApproach(metaInfo)
            }
        } else throw new Exception("Cannot read access entries")
    }

    def handleAccessControlFirstApproach(metaInfo, ModuleFile mf) {
        metaInfo.accessControl.each {
            AccessEntry ace = new AccessEntry()
            ace.uuidPrincipal = it.uuidprincipal
            ace.flags = it.flags
            ace.permissionSet.allowed = it.permissionSet.allowed
            ace.permissionSet.denied = it.permissionSet.denied
            mf.accessControl.accessEntries.add(ace)
        }
        return mf;
    }

    def handleAccessControlSecondApproach(metaInfo, ModuleFile mf) {
        metaInfo.accessControl.each {
            AccessEntry ace = new AccessEntry()
            ace.permissions = it.permissions
            ace.principal = it.principal
            mf.accessControl.accessEntries.add()
        }
        return mf;
    }

    def handleRelation(metaInfo, ModuleFile mf) {
        if (metaInfo.relations && isCollectionOrArray(metaInfo.relations)) {
            metaInfo.relations.each {
                Relation rel = new Relation()
                rel.element = it.element
                rel.invalidate = it.invalidate
                rel.type = RelationTypes(it.type)
                mf.relations.add(rel)
            }
        }
    }

    boolean isCollectionOrArray(object) {
        [Collection, Object[]].any { it.isAssignableFrom(object.getClass()) }
    }

}
