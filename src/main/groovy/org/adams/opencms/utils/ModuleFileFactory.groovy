package org.adams.opencms.utils

import org.adams.opencms.beans.ModuleFile
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


    static org.safehaus.uuid.UUID generateUUID(String key) {
        org.safehaus.uuid.UUID baseUUID = new org.safehaus.uuid.UUID(org.safehaus.uuid.UUID.NAMESPACE_URL)
        org.safehaus.uuid.UUID newUUID = UUIDGenerator.getInstance().generateNameBasedUUID(baseUUID, key)
        return newUUID
    }

}
