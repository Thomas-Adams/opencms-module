package org.adams.opencms.beans

class Property {

    PropertyType type
    String key
    Object value

    Property(PropertyType type, String key, Object value) {
        this.type = type
        this.key = key
        this.value = value
    }
}
