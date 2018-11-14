package org.adams.opencms.tasks

import org.adams.opencms.extension.OpenCmsExtension
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputDirectory

class OpenCmsPluginTask extends DefaultTask implements AccessExtension {

    File moduleDir
    File manifestFile
    OpenCmsExtension extension;

    @InputDirectory
    File getModuleDir() {
        return moduleDir
    }

    OpenCmsPluginTask() {
        this.extension = (OpenCmsExtension) this.getOpencmsExtension()
        this.moduleDir = this.project.file(this.opencmsExt('moduleDir'))
        this.manifestFile = new File(this.moduleDir.getAbsolutePath() + File.separator + this.opencmsExt('manifestFileName'))
    }
}
