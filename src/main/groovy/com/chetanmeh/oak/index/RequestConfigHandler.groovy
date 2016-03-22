package com.chetanmeh.oak.index

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
        if (fileName.endsWith("xml")){
            def text = Streams.asString(is, 'utf-8')
            return new XmlConfig(text).parse()
        } else if (fileName.endsWith("zip")){
            Indexes indexes = new Indexes()
            readFromContentPackage(is, indexes)
            indexes.afterPropertiesSet()
            return indexes
        } else if (fileName.endsWith("json")){
            def text = Streams.asString(is, 'utf-8')
            return new JsonConfig(text).parse()
        }
        return null
    }

    private static void readFromContentPackage(InputStream is, Indexes indexes) {
        ZipInputStream zis = new ZipInputStream(is)
        ZipEntry entry
        byte[] buffer = new byte[2048];
        while ((entry = zis.getNextEntry())) {
            if (entry.name.endsWith('/_oak_index/.content.xml')) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream()
                int len = 0
                while ((len = zis.read(buffer)) > 0) {
                    baos.write(buffer, 0, len);
                }
                new XmlConfig(baos.toString('utf-8')).parseTo(indexes)
            }
        }
    }
}
