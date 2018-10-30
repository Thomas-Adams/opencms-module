package org.adams.opencms.beans

class ResourceType {

    String clazz
    String name
    String id
    List<ResourceTypeProperty> properties = new ArrayList<>()
    Parameter param;

}
