package com.chetanmeh.oak.index.config.parser

import org.apache.commons.fileupload.servlet.ServletFileUpload
import org.apache.commons.fileupload.util.Streams

import javax.servlet.http.HttpServletRequest
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream


class RequestConfigHandler {

    static Indexes getIndexInfo(HttpServletRequest request){
        if (ServletFileUpload.isMultipartContent(request)){
            def upload = new ServletFileUpload();
            def itemItr = upload.getItemIterator(request)
            if (itemItr.hasNext()){
                def stream = itemItr.next()
                def name = stream.name
                def is = stream.openStream()
                try{
                    return getIndexInfo(name, is)
                } finally {
                    is?.close()
                }
            }
        }
    }

    static Indexes getIndexInfo(String fileName, InputStream is){
        if (fileName.endsWith("json")){
            def text = Streams.asString(is, 'utf-8')
            return new JsonConfig(text).parse()
        }
        return null
    }

}
