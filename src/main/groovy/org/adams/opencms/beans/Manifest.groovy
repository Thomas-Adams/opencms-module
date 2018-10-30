package org.adams.opencms.beans

import com.fasterxml.jackson.annotation.JsonProperty

class Manifest {

    Info info = new Info()
    Module module = new Module()

    @JsonProperty('files')
    ModuleFiles moduleFiles = new ModuleFiles()
}
