package org.adams.opencms.tasks

import org.adams.opencms.manifest.DependenciesXmlFromManifestParser
import org.gradle.api.tasks.TaskAction

class CreateDependenciesFromManifestTask extends OpenCmsPluginTask implements AccessExtension {

    @TaskAction
    void executeTask() {
        DependenciesXmlFromManifestParser parser = new DependenciesXmlFromManifestParser()
        parser.writeDependenciesXml(this.manifestFile)
    }

}
