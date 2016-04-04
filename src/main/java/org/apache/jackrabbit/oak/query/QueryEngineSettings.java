package org.apache.jackrabbit.oak.query;

//Stubbing the one in Oak as GAE does not allow MBeans
public class QueryEngineSettings {

    public long getLimitReads() {
        return Long.MAX_VALUE;
    }

    public boolean isSql2Optimisation() {
        return true;
    }

    public long getLimitInMemory() {
        return Long.MAX_VALUE;
    }

    public boolean getFullTextComparisonWithoutIndex() {
        return false;
    }
}
