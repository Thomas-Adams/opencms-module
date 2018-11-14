package org.adams.opencms.tasks


import org.adams.opencms.manifest.ExplorerTypesXmlFromManifestParser
import org.gradle.api.tasks.TaskAction

class CreateExplorerTypesFromManifestTask extends OpenCmsPluginTask implements AccessExtension {

    @TaskAction
    void executeTask() {
        ExplorerTypesXmlFromManifestParser parser = new ExplorerTypesXmlFromManifestParser()
        parser.writeExplorerTypesXml(this.manifestFile)
    }

}
