package org.adams.opencms.file

import groovy.json.JsonSlurper
import org.adams.opencms.beans.*
import org.apache.commons.io.FilenameUtils

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

    File moduleDir

    List<ModuleFile> moduleFiles = new ArrayList<ModuleFile>();

    def guessFileType(String fileName) {
        //TODO: configuration of file extensions and their corresponding file type
        if (new File(fileName).isDirectory())
            return 'folder'
        String ext = FilenameUtils.getExtension(fileName)
        if (ext.endsWith('txt') || ext.endsWith('css') || ext.endsWith('js') || ext.endsWith('json') || ext.endsWith('xml') || ext.endsWith('scss') || ext.endsWith('ts'))
            return 'plain'
        if (ext.endsWith('jsp'))
            return 'jsp'
        if (ext.endsWith('.png') || ext.endsWith('.gif') || ext.endsWith('jpg') || ext.endsWith('jpeg') || ext.endsWith('png') || ext.endsWith('tiff'))
            return 'image'
        if (ext.endsWith('xmlpage'))
            return 'xmlpage'
        if (ext.endsWith('containerpage'))
            return 'containerpage'
        return 'binary'
    }

    ModuleFileHandler(File moduleDir) {
        this.moduleDir = moduleDir
    }

    ModuleFileHandler() {
    }



    def isMetaFile(File file) {
        String name = file.getAbsolutePath()
        if (name.endsWith('_meta.json') || name.endsWith('dependencies.xml')
                || name.endsWith('resources.xml')
                || name.endsWith('parameters.xml')
                || name.endsWith('explorertypes.xml')
                || name.endsWith('resourcetypes.xml')
                || name.endsWith('exportpoints.xml')
                || name.endsWith('module.properties')
                || name.endsWith('manifest.xml')
                || name.endsWith('relations.xml')) {
            return true
        }
        return false
    }

    def List<ModuleFile> initModuleFiles() {


        moduleDir.eachFileRecurse {
            if (!isMetaFile(it)) {
                ModuleFile mf = new ModuleFile()
                mf.setType(guessFileType(it.getAbsolutePath()))
                String fileName = it.getAbsolutePath().minus(moduleDir.getAbsolutePath())
                if (fileName.startsWith('/') || fileName.startsWith('\\')) {
                    fileName = fileName.substring(1)
                }

                fileName = FilenameUtils.separatorsToUnix(fileName)
                println('Module filename ' + fileName)
                mf.setDestination(fileName)
                mf.flags = 0
                mf.dateCreated = new Date()

                //TODO: distinguish _meta.json for file and folder

                File meta = new File(it.getAbsolutePath() + '_meta.json')
                if (meta.exists()) {

                    metaInfo = new JsonSlurper().parse(meta, 'UTF-8')
                    if (metaInfo.type) {
                        mf.setType(meta.type)
                    } else {
                        mf.setType(guessFileType(it.getAbsolutePath()))
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

                    if (metaInfo.flags) {
                        mf.flags = Integer.parseInt(metaInfo.flags, 10)
                    }

                    if (metaInfo.dateCreated) {
                        mf.dateCreated = formatter.parse(metaInfo.dateCreated)
                    }
                    if (metaInfo.dateModified) {
                        mf.dateModified = formatter.parse(metaInfo.dateModified)
                    }

                    if (metaInfo.userCreated) {
                        mf.userCreated = metaInfo.userCreated
                    }
                    if (metaInfo.userLastModified) {
                        mf.userLastModified = metaInfo.userLastModified
                    }


                    handleAccessControl(metaInfo, mf)

                    handleRelation(metaInfo, mf)

                }
                moduleFiles.add(mf)
            }

        }
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
