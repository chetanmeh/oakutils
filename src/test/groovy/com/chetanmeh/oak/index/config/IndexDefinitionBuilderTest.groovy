package com.chetanmeh.oak.index.config

import org.apache.jackrabbit.oak.spi.state.NodeState
import org.apache.jackrabbit.oak.spi.state.NodeStateUtils
import org.junit.After
import org.junit.Test


class IndexDefinitionBuilderTest {
    private IndexDefinitionBuilder builder = new IndexDefinitionBuilder()

    @After
    public void dumpState(){
        println NodeStateUtils.toString(builder.build())
    }

    @Test
    public void defaultSetup() throws Exception{
        NodeState state = builder.build()
        assert state.getLong("compatVersion") == 2
        assert state.getString("async") == "async"
        assert state.getString("type") == "lucene"
    }

    @Test
    public void indexRule() throws Exception{
        builder.indexRule("nt:base")
            .property("foo")
                .ordered()
        .enclosingRule()
            .property("bar")
                .analyzed()
                .propertyIndex()
        .enclosingRule()
            .property("baz")
                .propertyIndex()

        NodeState state = builder.build()
        assert state.getChildNode('indexRules').exists()
        assert state.getChildNode('indexRules').getChildNode('nt:base').exists()
    }

    @Test
    public void aggregates() throws Exception{
        builder.aggregateRule('cq:Page').include('jcr:content').relativeNode()
        builder.aggregateRule('dam:Asset', '*', '*/*')

        NodeState state = builder.build()
        assert state.getChildNode('aggregates').exists()
        assert state.getChildNode('aggregates').getChildNode('dam:Asset').exists()
        assert state.getChildNode('aggregates').getChildNode('cq:Page').exists()

    }
}
