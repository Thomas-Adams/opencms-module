package org.adams.opencms.tasks


import org.adams.opencms.manifest.ResourcesXmlFromManifestParser
import org.gradle.api.tasks.TaskAction

class CreateResourcesFromManifestTask extends OpenCmsPluginTask implements AccessExtension {

    @TaskAction
    void executeTask() {
        ResourcesXmlFromManifestParser parser = new ResourcesXmlFromManifestParser()
        parser.writeResourcesXml(this.manifestFile)
    }

}
