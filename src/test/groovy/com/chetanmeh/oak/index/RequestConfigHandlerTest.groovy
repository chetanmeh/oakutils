package com.chetanmeh.oak.index

import com.chetanmeh.oak.index.config.parser.Indexes
import com.chetanmeh.oak.index.config.parser.RequestConfigHandler
import org.junit.Test;

class RequestConfigHandlerTest {
    @Test
    public void zip() throws Exception{
        File f = new File('/home/chetanm/data/rrt/fi/index-definitions/oak-index-definitions.zip')
        f.withInputStream {is ->
            Indexes idxs =  RequestConfigHandler.getIndexInfo(f.name, is)
            println idxs
        }
    }

}
