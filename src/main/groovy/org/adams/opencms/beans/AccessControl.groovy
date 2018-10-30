package org.adams.opencms.beans

import com.fasterxml.jackson.annotation.JsonProperty

class AccessControl {
    @JsonProperty("accessentries")
    List<AccessEntry> accessEntries
}
