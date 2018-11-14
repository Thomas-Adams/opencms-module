package org.adams.opencms.tasks

import org.adams.opencms.manifest.ManifestXmlParser
import org.gradle.api.tasks.TaskAction

class CreateMetaFilesFromManifestTask extends OpenCmsPluginTask implements AccessExtension {

    @TaskAction
    void executeTask() {
        File manifestFile = new File(this.moduleDir.getAbsolutePath() + '/manifest.xml');
        ManifestXmlParser parser = new ManifestXmlParser()
        parser.createMetaFiles(manifestFile, this.moduleDir, this.extension)
    }
}
