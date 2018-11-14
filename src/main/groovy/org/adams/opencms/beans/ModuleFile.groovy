package org.adams.opencms.beans

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.ToString

@ToString
class ModuleFile {

    String destination
    String source
    String type

    @JsonProperty("uuidstructure")
    String uuidStructure

    @JsonProperty("uuidresource")
    String uuidResource

    @JsonProperty("datelastmodified")
    Date dateLastModified

    @JsonProperty("userlastmodified")
    String userLastModified

    @JsonProperty("datecreated")
    Date dateCreated

    @JsonProperty("usercreated")
    String userCreated
    Integer flags
    List<Property> properties = new ArrayList<>()
    List<Relation> relations = new ArrayList<>()

    @JsonProperty("accesscontrol")
    AccessControl accessControl = new AccessControl()



}
