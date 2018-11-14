package org.adams.opencms.beans

import groovy.transform.AutoClone

@AutoClone
class ResourceType {

    String clazz
    String name
    String id
    List<ResourceTypeProperty> properties = new ArrayList<>()
    List<Parameter> params = new ArrayList<>();

}
