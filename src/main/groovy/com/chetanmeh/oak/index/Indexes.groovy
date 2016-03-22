package com.chetanmeh.oak.index


class Indexes {
    List<PropertyIndex> propertyIndexes = []
    List<LuceneIndex> luceneIndexes = []
    List<String> disabledIndexes = []
    PropertyIndex nodeTypeIndex
    Map<String, List<String>> duplicateRules = [:]

    int noOfIndexes(){
        return propertyIndexes.size() + luceneIndexes.size()
    }

    void afterPropertiesSet(){
        luceneIndexes.each {it.afterPropertiesSet()}
        propertyIndexes.each {it.afterPropertiesSet()}
        luceneIndexes.sort()
        propertyIndexes.sort{a, b ->
            a.declaringNodeTypes.size() <=> b.declaringNodeTypes.size() ?: a.path <=> b.path
        }
        extractNodeTypeIndex()
        moveGlobalFullTextToLast()
        identifyDuplicateRules()
    }

    private void identifyDuplicateRules() {
        Map<String, List<String>> ruleTypeToIndexMapping = [:].withDefault {[]}
        luceneIndexes.each {li ->
            li.rules.each {r ->
                ruleTypeToIndexMapping[r.type] << li.path
            }
        }

        ruleTypeToIndexMapping.each {k, v ->
            if(v.size() > 1 && k != 'nt:base'){
                duplicateRules[k] = v
            }
        }
    }

    private void moveGlobalFullTextToLast() {
        LuceneIndex global = luceneIndexes.find{it.path == '/oak:index/lucene'}
        luceneIndexes.removeAll {it.path == '/oak:index/lucene'}
        if (global) {
            luceneIndexes << global
        }
    }

    private void extractNodeTypeIndex() {
        nodeTypeIndex = propertyIndexes.find {it.propertyNames.contains('jcr:primaryType')}
        propertyIndexes.removeAll{it.propertyNames.contains('jcr:primaryType')}
    }
}
