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

    @Test
    public void multipleQueries() throws Exception{
        dumpIndex('''SELECT
  *
FROM [nt:base] AS a
WHERE
  a.[jcr:content/metadata/status] = 'published\'
ORDER BY
  a.[jcr:content/metadata/jcr:lastModified] DESC

select * from [nt:base] where foo = 1''')
    }

    def dumpIndex(String queryText){
        NodeState state = IndexConfigGeneratorHelper.getIndexConfig(queryText)
        println NodeStateExporter.toCND(state)
    }

    @Test
    public void xpath() throws Exception{
        dumpIndex('''/jcr:root/content/dam/element(*, dam:Asset)[@valid]''')

    }


}
