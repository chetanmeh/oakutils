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

}
