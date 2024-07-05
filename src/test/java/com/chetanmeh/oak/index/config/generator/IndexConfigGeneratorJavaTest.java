package com.chetanmeh.oak.index.config.generator;

import static org.junit.Assert.assertEquals;

import org.apache.jackrabbit.oak.spi.state.NodeState;
import org.junit.Test;

import com.chetanmeh.oak.state.export.JsonExporter;

public class IndexConfigGeneratorJavaTest {

    @Test
    public void testBadQuery() throws Exception {
        NodeState ns = IndexConfigGeneratorHelper.getIndexConfig("/jcr:root//*[@a]");
        JsonExporter ex = new JsonExporter();
        String index = ex.toJson(ns);
        assertEquals("{\n"
                + "  \"async\": \"async\",\n"
                + "  \"compatVersion\": 2,\n"
                + "  \"evaluatePathRestrictions\": true,\n"
                + "  \"jcr:primaryType\": \"oak:QueryIndexDefinition\",\n"
                + "  \"type\": \"lucene\",\n"
                + "  \"warningCommonNodeType\": \"Consider adding a more restrictive node type condition. Indexes on 'nt:base' or 'nt:unstructured' cover a lot of nodes, which increases the index size, and slows down query execution. Use a primary or mixin node type if possible.\",\n"
                + "  \"warningPathRestrictionMissing\": \"Consider adding a path restriction to the query. The query currently does not have a path restriction, that means the index will need to cover all nodes, including for example the version store. This will slow down index generation, and can increase the index size.\",\n"
                + "  \"warningTagMissing\": \"Consider adding a tag to the query, via 'option(index tag abc)'. See also https://jackrabbit.apache.org/oak/docs/query/query-engine.html#query-option-index-tag . The query currently does not have a tag, which can result in the wrong index to be used. Also, it prevents to add 'selectionPolicy' = 'tag' to the index definition, meaning that other, unrelated queries might use this index by mistake.\",\n"
                + "  \"indexRules\": {\n"
                + "    \"jcr:primaryType\": \"nt:unstructured\",\n"
                + "    \"nt:base\": {\n"
                + "      \"jcr:primaryType\": \"nt:unstructured\",\n"
                + "      \"properties\": {\n"
                + "        \"jcr:primaryType\": \"nt:unstructured\",\n"
                + "        \"a\": {\n"
                + "          \"jcr:primaryType\": \"nt:unstructured\",\n"
                + "          \"name\": \"a\",\n"
                + "          \"notNullCheckEnabled\": true,\n"
                + "          \"propertyIndex\": true\n"
                + "        }\n"
                + "      }\n"
                + "    }\n"
                + "  }\n"
                + "}", index);
    }
    
    @Test
    public void testQueryWithNodeType() throws Exception {
        NodeState ns = IndexConfigGeneratorHelper.getIndexConfig("/jcr:root//element(*, dam:Asset)[@a]");
        JsonExporter ex = new JsonExporter();
        String index = ex.toJson(ns);
        assertEquals("{\n"
                + "  \"async\": \"async\",\n"
                + "  \"compatVersion\": 2,\n"
                + "  \"evaluatePathRestrictions\": true,\n"
                + "  \"jcr:primaryType\": \"oak:QueryIndexDefinition\",\n"
                + "  \"type\": \"lucene\",\n"
                + "  \"warningPathRestrictionMissing\": \"Consider adding a path restriction to the query. The query currently does not have a path restriction, that means the index will need to cover all nodes, including for example the version store. This will slow down index generation, and can increase the index size.\",\n"
                + "  \"warningTagMissing\": \"Consider adding a tag to the query, via 'option(index tag abc)'. See also https://jackrabbit.apache.org/oak/docs/query/query-engine.html#query-option-index-tag . The query currently does not have a tag, which can result in the wrong index to be used. Also, it prevents to add 'selectionPolicy' = 'tag' to the index definition, meaning that other, unrelated queries might use this index by mistake.\",\n"
                + "  \"indexRules\": {\n"
                + "    \"jcr:primaryType\": \"nt:unstructured\",\n"
                + "    \"dam:Asset\": {\n"
                + "      \"jcr:primaryType\": \"nt:unstructured\",\n"
                + "      \"properties\": {\n"
                + "        \"jcr:primaryType\": \"nt:unstructured\",\n"
                + "        \"a\": {\n"
                + "          \"jcr:primaryType\": \"nt:unstructured\",\n"
                + "          \"name\": \"a\",\n"
                + "          \"notNullCheckEnabled\": true,\n"
                + "          \"propertyIndex\": true\n"
                + "        }\n"
                + "      }\n"
                + "    }\n"
                + "  }\n"
                + "}", index);
    }
    
    @Test
    public void testQueryWithTag() throws Exception {
        NodeState ns = IndexConfigGeneratorHelper.getIndexConfig(
                "/jcr:root//element(*, dam:Asset)[@a]" +
                "option(index tag abc)");
        JsonExporter ex = new JsonExporter();
        String index = ex.toJson(ns);
        assertEquals("{\n"
                + "  \"async\": \"async\",\n"
                + "  \"compatVersion\": 2,\n"
                + "  \"evaluatePathRestrictions\": true,\n"
                + "  \"jcr:primaryType\": \"oak:QueryIndexDefinition\",\n"
                + "  \"selectionPolicy\": \"tag\",\n"
                + "  \"tags\": [\"abc\"],\n"
                + "  \"type\": \"lucene\",\n"
                + "  \"warningPathRestrictionMissing\": \"Consider adding a path restriction to the query. The query currently does not have a path restriction, that means the index will need to cover all nodes, including for example the version store. This will slow down index generation, and can increase the index size.\",\n"
                + "  \"indexRules\": {\n"
                + "    \"jcr:primaryType\": \"nt:unstructured\",\n"
                + "    \"dam:Asset\": {\n"
                + "      \"jcr:primaryType\": \"nt:unstructured\",\n"
                + "      \"properties\": {\n"
                + "        \"jcr:primaryType\": \"nt:unstructured\",\n"
                + "        \"a\": {\n"
                + "          \"jcr:primaryType\": \"nt:unstructured\",\n"
                + "          \"name\": \"a\",\n"
                + "          \"notNullCheckEnabled\": true,\n"
                + "          \"propertyIndex\": true\n"
                + "        }\n"
                + "      }\n"
                + "    }\n"
                + "  }\n"
                + "}", index);
    }    

    @Test
    public void testGoodQuery() throws Exception {
        NodeState ns = IndexConfigGeneratorHelper.getIndexConfig(
                "/jcr:root/content/dam//element(*, dam:Asset)[@a]" +
                "option(index tag abc)");
        JsonExporter ex = new JsonExporter();
        String index = ex.toJson(ns);
        assertEquals("{\n"
                + "  \"async\": \"async\",\n"
                + "  \"compatVersion\": 2,\n"
                + "  \"evaluatePathRestrictions\": true,\n"
                + "  \"includedPaths\": [\"/content/dam\"],\n"
                + "  \"jcr:primaryType\": \"oak:QueryIndexDefinition\",\n"
                + "  \"queryPaths\": [\"/content/dam\"],\n"
                + "  \"selectionPolicy\": \"tag\",\n"
                + "  \"tags\": [\"abc\"],\n"
                + "  \"type\": \"lucene\",\n"
                + "  \"indexRules\": {\n"
                + "    \"jcr:primaryType\": \"nt:unstructured\",\n"
                + "    \"dam:Asset\": {\n"
                + "      \"jcr:primaryType\": \"nt:unstructured\",\n"
                + "      \"properties\": {\n"
                + "        \"jcr:primaryType\": \"nt:unstructured\",\n"
                + "        \"a\": {\n"
                + "          \"jcr:primaryType\": \"nt:unstructured\",\n"
                + "          \"name\": \"a\",\n"
                + "          \"notNullCheckEnabled\": true,\n"
                + "          \"propertyIndex\": true\n"
                + "        }\n"
                + "      }\n"
                + "    }\n"
                + "  }\n"
                + "}", index);
    }    

}
