package com.chetanmeh.oak.index.config.parser


class LuceneIndex implements Comparable<LuceneIndex>{
    String path
    int reindexCount
    int compatVersion
    boolean evaluatePathRestrictions
    List<String> excludedPaths
    List<String> includedPaths
    List<IndexRule> rules = []
    private String ruleTypes

    void afterPropertiesSet(){
        rules.each {it.afterPropertiesSet()}
        ruleTypes = rules.collect {it.type}.sort().join(',')
    }

    @Override
    int compareTo(LuceneIndex o) {
        return ruleTypes <=> o.ruleTypes
    }
}
