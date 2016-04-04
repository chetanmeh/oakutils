package com.chetanmeh.oak.index.config.generator;

import java.text.ParseException;
import java.util.Collections;
import java.util.List;

import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.PropertyType;
import javax.security.auth.login.LoginException;

import com.chetanmeh.oak.index.config.IndexDefinitionBuilder;
import com.google.appengine.labs.repackaged.com.google.common.collect.ImmutableList;
import org.apache.jackrabbit.oak.api.QueryEngine;
import org.apache.jackrabbit.oak.api.Root;
import org.apache.jackrabbit.oak.api.Type;
import org.apache.jackrabbit.oak.commons.PathUtils;
import org.apache.jackrabbit.oak.core.ImmutableRoot;
import org.apache.jackrabbit.oak.query.ExecutionContext;
import org.apache.jackrabbit.oak.query.QueryEngineImpl;
import org.apache.jackrabbit.oak.query.QueryEngineSettings;
import org.apache.jackrabbit.oak.spi.query.Cursor;
import org.apache.jackrabbit.oak.spi.query.Filter;
import org.apache.jackrabbit.oak.spi.query.Filter.PathRestriction;
import org.apache.jackrabbit.oak.spi.query.Filter.PropertyRestriction;
import org.apache.jackrabbit.oak.spi.query.QueryConstants;
import org.apache.jackrabbit.oak.spi.query.QueryIndex;
import org.apache.jackrabbit.oak.spi.query.QueryIndex.OrderEntry;
import org.apache.jackrabbit.oak.spi.query.QueryIndexProvider;
import org.apache.jackrabbit.oak.spi.state.NodeState;

import static com.chetanmeh.oak.index.config.IndexDefinitionBuilder.IndexRule;
import static com.chetanmeh.oak.index.config.IndexDefinitionBuilder.PropertyRule;
import static org.apache.jackrabbit.oak.plugins.nodetype.write.InitialContent.INITIAL_CONTENT;

class IndexConfigGenerator{
    private QueryEngine queryEngine;
    private IndexDefinitionBuilder builder = new IndexDefinitionBuilder();

    public IndexConfigGenerator() throws LoginException, NoSuchWorkspaceException {
        //On GAE instantiating the whole Oak class causes issues as some calls made by
        //Oak are blocked like those accessing MBeans
        final Root root = new ImmutableRoot(INITIAL_CONTENT);
        queryEngine = new QueryEngineImpl() {
            @Override
            protected ExecutionContext getExecutionContext() {
                return new ExecutionContext(
                        INITIAL_CONTENT, root,
                        new QueryEngineSettings(),
                        new LuceneIndexGeneratingIndexProvider(), null);
            }
        };
    }

    public void process(String statement) throws ParseException {
        String lang = statement.startsWith("/") ? "xpath" : "JCR-SQL2";
        process(statement, lang);
    }

    public void process(String statement, String language) throws ParseException {
        String lang = statement.startsWith("/") ? "xpath" : "JCR-SQL2";
        queryEngine.executeQuery(statement, lang, null, null);
    }

    public NodeState getIndexConfig(){
        return builder.build();
    }

    private void processFilter(Filter filter, List<OrderEntry> sortOrder) {
        processPathRestriction(filter);
        IndexRule rule = processNodeTypeConstraint(filter);
        processPropertyRestrictions(filter, rule);
        processSortConditions(sortOrder, rule);
        //TODO Fulltext constraints
    }

    private void processSortConditions(List<OrderEntry> sortOrder, IndexRule rule) {
        if (sortOrder == null){
            return;
        }

        for (OrderEntry o : sortOrder){
            if ("jcr:score".equals(o.getPropertyName())){
                continue;
            }

            if (o.getPropertyType().isArray()) {
                continue;
            }

            PropertyRule propRule = rule.property(o.getPropertyName());

            if (o.getPropertyType() != Type.UNDEFINED){
                propRule.ordered(PropertyType.nameFromValue(o.getPropertyType().tag()));
            } else {
                propRule.ordered();
            }
        }
    }

    private void processPropertyRestrictions(Filter filter, IndexRule rule) {
        for (PropertyRestriction pr : filter.getPropertyRestrictions()){
            //Ignore special restrictions
            if (isSpecialRestriction(pr)){
                continue;
            }
            PropertyRule propRule = rule.property(pr.propertyName);
            if (pr.isNullRestriction()){
                propRule.nullCheckEnabled();
            }
            propRule.propertyIndex();
        }

        if (filter.getPropertyRestriction(QueryConstants.RESTRICTION_LOCAL_NAME) != null){
            rule.indexNodeName();
        }
    }

    private boolean isSpecialRestriction(PropertyRestriction pr) {
        String name = pr.propertyName;
        if (name.startsWith(":")){
            return true;
        }
        if (name.startsWith("native*")){
            return true;
        }
        return false;
    }

    private void processPathRestriction(Filter filter) {
        if (filter.getPathRestriction() != PathRestriction.NO_RESTRICTION
                || (filter.getPathRestriction() == PathRestriction.ALL_CHILDREN
                        && !PathUtils.denotesRoot(filter.getPath()))
                ){
            builder.evaluatePathRestrictions();
        }
    }

    private IndexRule processNodeTypeConstraint(Filter filter) {
        return builder.indexRule(filter.getNodeType());
    }

    private class LuceneIndexGeneratingIndexProvider implements QueryIndexProvider{
        @Override
        public List<? extends QueryIndex> getQueryIndexes(NodeState nodeState) {
            return ImmutableList.of(new LuceneIndexGeneratingIndex());
        }
    }

    private class LuceneIndexGeneratingIndex implements QueryIndex.AdvancedQueryIndex, QueryIndex {
        @Override
        public double getMinimumCost() {
            return 1.0;
        }

        @Override
        public double getCost(Filter filter, NodeState nodeState) {
            return Double.MAX_VALUE;
        }

        @Override
        public Cursor query(Filter filter, NodeState nodeState) {
            return null;
        }

        @Override
        public String getPlan(Filter filter, NodeState nodeState) {
            return null;
        }

        @Override
        public String getIndexName() {
            return "LuceneIndexGenerator";
        }

        @Override
        public List<QueryIndex.IndexPlan> getPlans(Filter filter,
                                                   List<OrderEntry> sortOrder, NodeState rootState) {
            processFilter(filter, sortOrder);
            return Collections.emptyList();
        }

        @Override
        public String getPlanDescription(QueryIndex.IndexPlan plan, NodeState root) {
            return null;
        }

        @Override
        public Cursor query(QueryIndex.IndexPlan plan, NodeState rootState) {
            return null;
        }
    }
}
