package org.adams.opencms.beans

import com.fasterxml.jackson.annotation.JsonProperty

class AccessEntry {

    String principal
    String permissions

    @JsonProperty("uuidprincipal")
    String uuidPrincipal
    int flags
    @JsonProperty("permissionset")
    PermissionSet permissionSet = new PermissionSet()

}
