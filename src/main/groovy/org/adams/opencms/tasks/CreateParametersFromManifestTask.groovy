package org.adams.opencms.tasks


import org.adams.opencms.manifest.ParametersXmlFromManifestParser
import org.gradle.api.tasks.TaskAction

class CreateParametersFromManifestTask extends OpenCmsPluginTask implements AccessExtension {

    @TaskAction
    void executeTask() {
        ParametersXmlFromManifestParser parser = new ParametersXmlFromManifestParser()
        parser.writeParametersXml(this.manifestFile)
    }
}
