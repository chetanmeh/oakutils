package com.chetanmeh.oak.index

import groovy.json.JsonSlurper

class JsonConfig {
    final String json

    public JsonConfig(String json){
        this.json = json
    }

    public Indexes parse(){
        Indexes indexes = new Indexes()
        parseTo(indexes)
        indexes.afterPropertiesSet()
        return indexes
    }

    public void parseTo(Indexes indexes){
        def content = new JsonSlurper().parseText(json)
        filtered(content).each {k,v ->
            switch(v.type){
                case 'property' :
                    indexes.propertyIndexes << parsePropertyIndexDefn(k,v)
                    break
                case 'lucene' :
                    indexes.luceneIndexes << parseLuceneIndexDefn(k,v)
                    break
                case 'disabled' :
                    indexes.disabledIndexes << k
                    break
            }
        }
    }

    LuceneIndex parseLuceneIndexDefn(def name, Map v) {
        LuceneIndex li = new LuceneIndex()
        li.compatVersion = v.compatVersion ?: 0
        li.path = "/oak:index/$name"
        li.evaluatePathRestrictions = v.evaluatePathRestrictions ?: false
        li.includedPaths = v.includedPaths ?: []
        li.excludedPaths = v.excludedPaths ?: []

        filtered(v.indexRules).each { ruleName, rule ->
            IndexRule ir = new IndexRule()
            ir.type = ruleName
            filtered(rule.get('properties')).each{pn, Map p ->
                PropertyDefinition pd = new PropertyDefinition()
                pd.name = p.name
                pd.ordered = toBool(p.ordered, false)
                pd.propertyIndex = toBool(p.propertyIndex, false)
                pd.isRegexp = toBool(p.isRegexp, false)
                pd.nullCheckEnabled = toBool(p.nullCheckEnabled, false)
                pd.index = toBool(p.index, true)

                ir.properties << pd
            }

            li.rules << ir
        }
        return li
    }

    PropertyIndex parsePropertyIndexDefn(def name, Map v) {
        PropertyIndex pi = new PropertyIndex()
        pi.path = "/oak:index/$name"
        pi.declaringNodeTypes = v.declaringNodeTypes ?: []
        pi.propertyNames = v.propertyNames ?: []
        pi.unique = v.unique ?: false
        pi.includedPaths = v.includedPaths ?: []
        pi.excludedPaths = v.excludedPaths ?: []
        return pi
    }

    /**
     * Strip of keys like jcr:primaryType by only exposing map type of values
     */
    private Map filtered(Map m){
        m.findAll {k,v -> v instanceof Map}
    }

    private boolean toBool(def v, boolean defaultVal){
        return v == null ? defaultVal : v
    }
}
