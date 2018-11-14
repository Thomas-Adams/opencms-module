package org.adams.opencms.tasks


import org.adams.opencms.manifest.ManifestBuilder
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

import java.text.SimpleDateFormat

class ManifestTask extends OpenCmsPluginTask implements AccessExtension {

    File manifestFile;

    @OutputFile
    File getManifestFile() {
        return manifestFile
    }


    void init() {
        manifestFile = new File(getProject().getBuildDir().getAbsolutePath() + File.separator + getExtension().moduleName + '_' + getExtension().moduleVersion
                + File.separator + getExtension().manifestFileName)
    }

    @TaskAction
    void executeTask() {
        init()
        createManifestFile()
    }

    def createManifestFile() {
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z")
        def root = "<export></export>"
        if (!manifestFile.exists()) {
            manifestFile.parentFile.mkdirs()
            manifestFile.createNewFile()
        }
        ManifestBuilder manifestBuilder = new ManifestBuilder()
        manifestBuilder.createManifestFile(manifestFile, moduleDir, getExtension())
    }
}
