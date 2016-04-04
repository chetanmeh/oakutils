package com.chetanmeh.oak.state.export

import com.chetanmeh.oak.index.config.IndexDefinitionBuilder
import org.apache.jackrabbit.oak.spi.state.NodeState
import org.junit.Test

class ExporterTest {

    @Test
    public void jsonOutput() throws Exception{
        println NodeStateExporter.toJson(createState())
    }

    @Test
    public void cnd() throws Exception{
        println NodeStateExporter.toCND(createState())
    }

    @Test
    public void xml() throws Exception{
        println NodeStateExporter.toXml(createState())
    }

    private static NodeState createState(){
        IndexDefinitionBuilder builder = new IndexDefinitionBuilder()
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

        builder.includedPaths("/content", "/etc")
        builder.aggregateRule('cq:Page').include('jcr:content').relativeNode()
        builder.aggregateRule('dam:Asset', '*', '*/*')

        return builder.build()
    }
}
