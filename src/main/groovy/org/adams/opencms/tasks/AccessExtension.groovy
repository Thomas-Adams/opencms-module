package org.adams.opencms.tasks

import org.adams.opencms.OpenCmsModulePlugin

trait AccessExtension {
    /**
     * Since Project API throws exception if asked for a non-existing extension it gets wrapped here for convenience
     * @return
     */
    def opencmsExt(String key) {
        return opencmsExt(key,null)
    }


    def getOpencmsExtension() {
        return this.getProject().getProperties().get(OpenCmsModulePlugin.EXT_NAME);
    }

    def opencmsExt(String key, def defaultValue) {
        // full access path out, makes it easier to mock
        if(this.getProject().hasProperty(OpenCmsModulePlugin.EXT_NAME))
            return this.getProject().getProperties().get(OpenCmsModulePlugin.EXT_NAME)?."${key}" ?: defaultValue
        else
            return defaultValue
    }

}