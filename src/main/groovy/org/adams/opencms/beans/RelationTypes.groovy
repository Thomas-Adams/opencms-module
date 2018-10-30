package org.adams.opencms.beans

enum RelationTypes {

    WEAK('WEAK'), STRONG('STRONG')

    private String typeName

    private RelationTypes(String typeName) {
        this.typeName = typeName.toUpperCase()
    }
}