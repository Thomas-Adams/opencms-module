package org.adams.opencms.utils

import groovy.json.JsonSlurper
import org.adams.opencms.beans.ModuleFile
import org.adams.opencms.beans.Property
import org.adams.opencms.beans.PropertyType
import org.adams.opencms.extension.OpenCmsExtension
import org.apache.commons.io.FilenameUtils
import org.safehaus.uuid.UUIDGenerator

import java.text.SimpleDateFormat

class ModuleFileFactory {


    static String guessFileType(String fileName) {
        //TODO: configuration of file extensions and their corresponding file type
        if (new File(fileName).isDirectory())
            return 'folder'
        String ext = FilenameUtils.getExtension(fileName)
        if (ext.endsWith('txt') || ext.endsWith('css') || ext.endsWith('pom') || ext.endsWith('js') || ext.endsWith('json') || ext.endsWith('xml') || ext
                .endsWith('scss') || ext.endsWith('ts'))
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

    static boolean isMetaFile(File file) {
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

    static ModuleFile fromFile(File moduleFile, File moduleDir) {
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z")

        ModuleFile mf = new ModuleFile()
        mf.type = guessFileType(moduleFile.getAbsolutePath())
        String destination = moduleFile.toPath().toString().substring(moduleDir.toPath().toString().length() + 1)
        destination = FilenameUtils.separatorsToUnix(destination)
        mf.destination = destination
        if (moduleFile.isFile()) {
            mf.source = destination
        }
        mf.uuidStructure = generateUUID(destination)
        mf.userLastModified = 'Admin'
        mf.userCreated = 'Admin'
        mf.dateLastModified = new Date()
        mf.dateCreated = new Date()
        mf.flags = 0
        return mf
    }

    static ModuleFile forJarFile(File moduleDir, OpenCmsExtension extension) {
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z")
        ModuleFile mf = new ModuleFile()
        mf.type ='binary'
        String destination = 'system/modules/' + extension.moduleName + '/lib/' + extension.jarFileName
        mf.source =destination
        mf.destination =destination

        File meta = new File(moduleDir.getAbsolutePath() + File.separator + destination + '_meta.json')
        if (meta.exists()) {
            def metaInfo = new JsonSlurper().parse(meta, 'UTF-8')
            if (metaInfo.source) {
                mf.setSource(metaInfo.source)
            }
            if (meta.isFile()) {
                if (!metaInfo.source) {
                    mf.source = destination
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
                mf.setType(guessFileType(meta.getAbsolutePath()))
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
            } else {
                mf.uuidStructure = generateUUID(destination)
            }
            if (metaInfo.uuidresource) {
                mf.uuidResource = metaInfo.uuidresource
            }

        }
        mf.uuidStructure = generateUUID(destination)
        mf.userLastModified = 'Admin'
        mf.userCreated = 'Admin'
        mf.dateLastModified = new Date()
        mf.dateCreated = new Date()
        mf.flags = 0
        return mf
    }

    static org.safehaus.uuid.UUID generateUUID(String key) {
        org.safehaus.uuid.UUID baseUUID = new org.safehaus.uuid.UUID(org.safehaus.uuid.UUID.NAMESPACE_URL)
        org.safehaus.uuid.UUID newUUID = UUIDGenerator.getInstance().generateNameBasedUUID(baseUUID, key)
        return newUUID
    }

}
