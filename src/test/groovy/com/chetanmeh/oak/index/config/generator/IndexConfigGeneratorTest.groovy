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

}
