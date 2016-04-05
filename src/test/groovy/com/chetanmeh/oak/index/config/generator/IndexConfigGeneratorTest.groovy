package com.chetanmeh.oak.index.config.generator

import com.chetanmeh.oak.state.export.NodeStateExporter
import org.junit.After
import org.junit.Test


class IndexConfigGeneratorTest {
    IndexConfigGenerator generator = new IndexConfigGenerator()

    @After
    public void dumpIndexConfig(){
        println NodeStateExporter.toCND(generator.indexConfig);
    }

    @Test
    void generateIndexConfig(){
        generator.process("select * from [nt:base] where foo = 'bar'")
    }

    @Test
    void generateIndexConfig2(){
//        generator.process("SELECT  *  FROM [dam:Asset] AS a  WHERE   a.[jcr:content/metadata/status] = 'published'  ORDER BY  a.[jcr:content/metadata/jcr:lastModified] DESC")
        generator.process("SELECT  *  FROM [dam:Asset] AS a  WHERE   a.[jcr:content/metadata/status] = 'published'  " +
                "ORDER BY  a.[jcr:content/metadata/jcr:lastModified] DESC")
    }

    @Test
    public void fulltext() throws Exception{
        generator.process('''SELECT
  *
FROM [app:Asset]
WHERE
  CONTAINS([jcr:content/metadata/comment], 'december')''')
    }


    @Test
    public void fulltext2() throws Exception{
        generator.process('''SELECT
  *
FROM [app:Asset]
WHERE
  CONTAINS([jcr:content/metadata/*], 'december')''')
    }

    @Test
    public void fulltext3() throws Exception{
        generator.process('''SELECT
  *
FROM [app:Asset]
WHERE
  CONTAINS([month], 'december')''')
    }
}
