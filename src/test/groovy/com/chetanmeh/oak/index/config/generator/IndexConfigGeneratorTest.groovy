package com.chetanmeh.oak.index.config.generator

import com.chetanmeh.oak.state.export.NodeStateExporter
import org.junit.After
import org.junit.Before
import org.junit.Test


class IndexConfigGeneratorTest {
    IndexConfigGenerator generator

    @Before
    void setup() {
        generator = new IndexConfigGenerator()
    }

    @After
    void dumpIndexConfig() {
        println NodeStateExporter.toCND(generator.indexConfig);
    }

    @Test
    void generateIndexConfig() {
        processQuery("select * from [nt:base] where foo = 'bar'")
    }

    @Test
    void generateIndexConfig2() {
        processQuery("SELECT  *  FROM [dam:Asset] AS a  WHERE   a.[jcr:content/metadata/status] = 'published'  " +
                "ORDER BY  a.[jcr:content/metadata/jcr:lastModified] DESC")
    }

    @Test
    void fulltext() throws Exception {
        processQuery('''SELECT
                                      *
                                    FROM [app:Asset]
                                    WHERE
                                      CONTAINS([jcr:content/metadata/comment], 'december')''')
    }


    @Test
    void fulltext2() throws Exception {
        processQuery('''SELECT
                              *
                            FROM [app:Asset]
                            WHERE
                              CONTAINS([jcr:content/metadata/*], 'december')''')
    }

    @Test
    void testPathRestrictions() throws Exception {
        processQuery("/jcr:root/content");
        String res = NodeStateExporter.toCND(generator.indexConfig);
        assert res.contains("includedPaths = [/content]")
        assert res.contains("queryPaths = [/content]")

        processQuery("select * from [nt:base] where isdescendantnode('/content/dam')")
        res = NodeStateExporter.toCND(generator.indexConfig);
        assert res.contains("includedPaths = [/content/dam]")
        assert res.contains("queryPaths = [/content/dam]")
    }

    @Test
    void testHasFunctionIndex() throws Exception {
        processQuery("""
                    select * from [cq:Page] as a
                    where lower([jcr:title])='france'
                    and isdescendantnode(a, '/ content/wknd')
                """
        )
        String res = NodeStateExporter.toCND(generator.indexConfig)
        assert res.contains("includedPaths = [/content/wknd]")
        assert res.contains("functionName = \"lower([jcr:title])\"")
    }

    @Test
    void test() throws Exception {
        processQuery("""
                    select * from [cq:PageContent] as a 
                    where name()='jcr:content' 
                    and isdescendantnode(a, '/content/wknd')
                """
        )
    }

    @Test
    void testLowerCase() {
        processQuery("""/jcr:root/content/wknd// element(*, cq:Page) [fn:lower-case(@jcr:title) = 'france']""")
        assert indexConfigHasNode("cq:Page", "lowerCaseTitle")
    }

    @Test
    void testXPathCategorizer() {
        String query = "select [jcr:path], [jcr:score], * from [nt:base] as a where issamenode(a, '/content') /* xpath: /jcr:root/content */"
        assert IndexConfigGenerator.isOriginallyXPath(query)
    }


    private void processQuery(String query) {
        generator.process(query)
    }

    private boolean indexConfigHasNode(String child, String property) {
        return generator.getIndexConfig()
                .getChildNode("indexRules")
                .getChildNode(child)
                .getChildNode("properties")
                .getChildNode(property).exists()
    }
}
