package com.chetanmeh.oak.index


class PropertyDefinition {
    String name
    boolean propertyIndex
    boolean ordered
    boolean index
    Map<String, String> attrs
    boolean isRegexp
    boolean analyzed

    boolean nullCheckEnabled
}
