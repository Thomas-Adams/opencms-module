package org.adams.opencms.tasks


import org.adams.opencms.manifest.ResourceTypesXmlFromManifestParser
import org.gradle.api.tasks.TaskAction

class CreateResourceTypesFromManifestTask extends OpenCmsPluginTask implements AccessExtension {

    @TaskAction
    void executeTask() {
        ResourceTypesXmlFromManifestParser parser = new ResourceTypesXmlFromManifestParser()
        parser.writeResourceTypesXml(this.manifestFile)
    }
}
