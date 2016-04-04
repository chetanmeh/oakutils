package com.chetanmeh.oak.index.config.parser


class PropertyDefinition {
    String name
    boolean propertyIndex
    boolean ordered
    boolean index
    Map<String, String> attrs
    boolean isRegexp
    boolean analyzed

    boolean nullCheckEnabled
    boolean useInExcerpt
    boolean nodeScopeIndex
    boolean useInSuggest
    boolean useInSpellcheck

    boolean facets
}
