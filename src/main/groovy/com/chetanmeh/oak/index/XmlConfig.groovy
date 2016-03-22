package com.chetanmeh.oak.index

import groovy.util.slurpersupport.GPathResult


class XmlConfig {
    final String xml

    public XmlConfig(String xml){
        this.xml = xml
    }

    public Indexes parse(){
        Indexes indexes = new Indexes()
        parseTo(indexes)
        indexes.afterPropertiesSet()
        return indexes
    }

    public void parseTo(Indexes indexes){
        def content = new XmlSlurper(false, false).parseText(xml)
        content.children().each {idx ->
            switch (idx.@type){
                case 'property' :
                    indexes.propertyIndexes << parsePropertyIndexDefn(idx)
                    break
                case 'lucene' :
                    indexes.luceneIndexes << parseLuceneIndexDefn(idx)
                    break
                case 'disabled' :
                    indexes.disabledIndexes << idx.name()
                    break
            }
        }
    }

    LuceneIndex parseLuceneIndexDefn(def idx) {
        LuceneIndex li = new LuceneIndex()
        if (hasAttr(idx, 'compatVersion')) {
            li.compatVersion = parseJcrValue(idx.@compatVersion.text()) as int
        }
        li.path = "/oak:index/${idx.name()}"
        li.evaluatePathRestrictions = parseJcrValue(idx.@evaluatePathRestrictions.text()) as boolean
        li.includedPaths = parseJcrArray(idx.@includedPaths.text())
        li.excludedPaths = parseJcrArray(idx.@excludedPaths.text())

        idx.indexRules.children().each {GPathResult irConf ->
            IndexRule ir = new IndexRule()
            ir.type = irConf.name()
            irConf.properties.children().each{p ->
                PropertyDefinition pd = new PropertyDefinition()
                pd.name = p.@name.text()
                pd.ordered = toBool(p, 'ordered', false)
                pd.propertyIndex = toBool(p, 'propertyIndex', false)
                pd.isRegexp = toBool(p, 'isRegexp', false)
                pd.nullCheckEnabled = toBool(p, 'nullCheckEnabled', false)
                pd.index = toBool(p, 'index', true)
                pd.useInExcerpt = toBool(p, 'useInExcerpt', false)
                pd.nodeScopeIndex = toBool(p, 'nodeScopeIndex', false)
                pd.useInSuggest = toBool(p, 'useInSuggest', false)
                pd.useInSpellcheck = toBool(p, 'useInSpellcheck', false)
                pd.facets = toBool(p, 'facets', false)

                ir.properties << pd
            }

            li.rules << ir
        }
        return li
    }

    PropertyIndex parsePropertyIndexDefn(def idx) {
        PropertyIndex pi = new PropertyIndex()
        pi.path = "/oak:index/${idx.name()}"
        pi.declaringNodeTypes = parseJcrArray(idx.@declaringNodeTypes.text())
        pi.propertyNames = parseJcrArray(idx.@propertyNames.text())
        pi.unique = toBool(idx, 'unique', false)
        pi.includedPaths = parseJcrArray(idx.@includedPaths.text())
        pi.excludedPaths = parseJcrArray(idx.@excludedPaths.text())
        return pi
    }

    private static boolean toBool(GPathResult xml, String attrName, boolean defaultValue){
        def prop = xml['@' + attrName]
        if (prop && !prop.isEmpty()){
            return parseJcrValue(prop.text()).toBoolean()
        }
        return defaultValue
    }

    private static boolean hasAttr(GPathResult xml, String attrName){
        def prop = xml['@' + attrName]
        if (prop && !prop.isEmpty()){
            return true
        }
        return false
    }

    static def parseJcrValue(String value){
        if (!value){
            return null
        }
        if (value.contains('}')) {
            return value.substring(value.indexOf('}') + 1)
        }
        return value
    }

    static def parseJcrArray(String value){
        if (value.endsWith("]")){
            String csv = value.substring(value.indexOf('[') + 1, value.indexOf(']'))
            return csv.tokenize(',')
        }
        return []
    }
}
