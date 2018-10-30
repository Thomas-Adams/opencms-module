package org.adams.opencms.beans

import com.fasterxml.jackson.annotation.JsonProperty


class Module {

    String name
    String nicename
    String group
    @JsonProperty("class")
    String clazz
    @JsonProperty("import-script")
    String importScript
    @JsonProperty("export-mode")
    String exportMode = "default"
    String description
    String version
    String authorname
    String authoremail
    @JsonProperty("datecreated")
    Date dateCreated
    @JsonProperty("dateinstalled")
    Date dateInstalled
    @JsonProperty("userinstalled")
    String userInstalled
    List<Dependency> dependencies = new ArrayList<>()
    @JsonProperty("exportpoints")
    List<ExportPoint> exportPoints = new ArrayList<>()
    List<Relation> relations = new ArrayList<>()
    List<Resource> resources = new ArrayList<>()
    List<Parameter> parameters = new ArrayList<>()
    @JsonProperty("excludedresources")
    List<Resource> excludeResources = new ArrayList<>()
    List<Resource> libs = new ArrayList<>()
    Info info= new Info()
    List<ModuleFile> moduleFiles = new ArrayList<>()
    List<ResourceType> resourceTypes = new ArrayList<>()
    List<ExplorerType> explorerTypes = new ArrayList<>()
}
