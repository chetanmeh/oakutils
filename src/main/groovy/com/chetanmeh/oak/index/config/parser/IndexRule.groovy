package com.chetanmeh.oak.index.config.parser


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
