package com.chetanmeh.oak.state.export;

import org.apache.jackrabbit.oak.spi.state.NodeState;
import org.junit.Test;

import com.chetanmeh.oak.index.config.IndexDefinitionBuilder;

public class ExporterTest {
    @Test
    public void jsonOutput() throws Exception {
        System.out.println(NodeStateExporter.toJson(createState()));
    }

    @Test
    public void cnd() throws Exception {
        System.out.println(NodeStateExporter.toCND(createState()));
    }

    @Test
    public void xml() throws Exception {
        System.out.println(NodeStateExporter.toXml(createState()));
    }

    private static NodeState createState() {
        IndexDefinitionBuilder builder = new IndexDefinitionBuilder();
        builder.indexRule("nt:base").property("foo").ordered().enclosingRule().property("bar").analyzed()
                .propertyIndex().enclosingRule().property("baz").propertyIndex();

        builder.includedPaths("/content", "/etc");
        builder.aggregateRule("cq:Page").include("jcr:content").relativeNode();
        builder.aggregateRule("dam:Asset", "*", "*/*");

        return builder.build();
    }
}
