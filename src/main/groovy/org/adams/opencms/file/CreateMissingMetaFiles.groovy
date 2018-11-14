package org.adams.opencms.file

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import groovy.io.FileType
import groovy.json.JsonOutput
import org.adams.opencms.beans.ModuleFile
import org.adams.opencms.utils.ModuleFileFactory

import java.text.SimpleDateFormat

class CreateMissingMetaFiles {
    private static final String DATE_FORMAT = 'EEE, dd MMM yyyy HH:mm:ss z'

    List<File> listFilesWithMissingMetaInfo(File moduleDir) {
        List<File> fileList = new ArrayList<>()

        moduleDir.eachFileRecurse(FileType.ANY) {
            if (!it.getAbsolutePath().endsWith('_meta.json') && !it.getName().equals("manifest.xml")) {
                String name = ''
                if (it.isDirectory()) {
                    name = it.getAbsolutePath() + File.separator + '_meta.json'
                } else {
                    name = it.getAbsolutePath() + '_meta.json'
                }
                File f = new File(name)
                if (!f.exists()) {
                    fileList.add(it)
                }
            }
        }
        return fileList
    }

    void createMissingMetaFileFromFile(File moduleDir, File file) {
        String name = ''
        if (file.isDirectory()) {
            name = file.getAbsolutePath() + File.separator + '_meta.json'
        } else {
            name = file.getAbsolutePath() + '_meta.json'
        }

        File metaFile = new File(name)
        FileWriter fileWriter = new FileWriter(metaFile)

        ObjectMapper objectMapper = new ObjectMapper()
        objectMapper.setDateFormat(new SimpleDateFormat(DATE_FORMAT))
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_ABSENT);

        ModuleFile mf = ModuleFileFactory.fromFile(file, moduleDir)
        String json = objectMapper.writeValueAsString(mf)
        fileWriter.write(JsonOutput.prettyPrint(json))
        fileWriter.flush()
        fileWriter.close()
    }

    void createMissingMetaFiles(File moduleDir) {
        List<File> fileList = listFilesWithMissingMetaInfo(moduleDir)
        fileList.each { it ->
            createMissingMetaFileFromFile(moduleDir, it)
        }
    }
}
