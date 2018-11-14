package org.adams.opencms.tasks


import org.adams.opencms.manifest.ExportPointsXmlFromManifestParser
import org.gradle.api.tasks.TaskAction

class CreateExportPointsFromManifestTask extends OpenCmsPluginTask implements AccessExtension {

    @TaskAction
    void executeTask() {
        ExportPointsXmlFromManifestParser parser = new ExportPointsXmlFromManifestParser()
        parser.writeExportpointsXml(this.manifestFile)
    }

}
