package org.adams.opencms.tasks


import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile

class OpenCmsPluginTask extends DefaultTask implements AccessExtension {

    @InputDirectory
    File moduleDir = project.file(this.opencmsExt('moduleDir'))

    @InputFile
    File manifestFile = project.file(this.opencmsExt('moduleDir'))

}
