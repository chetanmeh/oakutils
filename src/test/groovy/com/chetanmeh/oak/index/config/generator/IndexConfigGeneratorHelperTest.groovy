package com.chetanmeh.oak.index.config.generator

import com.chetanmeh.oak.state.export.NodeStateExporter
import org.apache.jackrabbit.oak.spi.state.NodeState
import org.junit.Test


class IndexConfigGeneratorHelperTest {

    @Test
    public void simpleCase() throws Exception{
        dumpIndex('''#Paste your queries here

select * from [nt:base] where foo = 'bar\'
''')
    }

    def dumpIndex(String queryText){
        NodeState state = IndexConfigGeneratorHelper.getIndexConfig(queryText)
        println NodeStateExporter.toCND(state)
    }
}
