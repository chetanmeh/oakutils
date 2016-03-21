package com.chetanmeh.oak.index


class IndexRule {
    String type
    List<PropertyDefinition> properties = []

    PropertyDefinition getPropDefn(String name){
        properties.find {it.name == name}
    }

    void afterPropertiesSet(){
        properties.sort{it.name}
    }
}
