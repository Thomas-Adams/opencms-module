package org.adams.opencms.tasks

import org.adams.opencms.file.CreateMissingMetaFiles
import org.gradle.api.tasks.TaskAction

class CreateMissingMetaFilesTask extends OpenCmsPluginTask implements AccessExtension {


    @TaskAction
    createMissingMetaFiles() {
        CreateMissingMetaFiles createMissingMetaFiles = new CreateMissingMetaFiles()
        createMissingMetaFiles.createMissingMetaFiles(this.moduleDir)
    }
}
