package com.chetanmeh.oak.index


class PropertyIndex {
    String path
    boolean unique
    List<String> excludedPaths = []
    List<String> includedPaths = []
    List<String> declaringNodeTypes = []
    List<String> propertyNames = []
    int reindexCount

    void afterPropertiesSet(){
        declaringNodeTypes.sort()
    }

    def getExcludedPaths() {
        return toString(excludedPaths)
    }

    def getIncludedPaths() {
        return toString(includedPaths)
    }

    def getDeclaringNodeTypes() {
        return toString(declaringNodeTypes)
    }
    def getDeclaringNodeTypesList() {
        return declaringNodeTypes
    }


    def getPropertyNames() {
        return toString(propertyNames)
    }

    private String toString(List<String> values){
        if (values){
            return values.size() == 1 ? values[0] : values.toString()
        }
        return ""
    }
}
