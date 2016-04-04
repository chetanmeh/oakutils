package com.chetanmeh.oak.index.config.generator;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;

import org.apache.jackrabbit.oak.spi.state.NodeState;

public class IndexConfigGeneratorHelper {

    public static NodeState getIndexConfig(String queryText) throws Exception {
        IndexConfigGenerator generator = new IndexConfigGenerator();
        extractQueriesAndGenerateIndex(queryText, generator);
        NodeState result = generator.getIndexConfig();
        return result;
    }

    private static void extractQueriesAndGenerateIndex(String queryText, IndexConfigGenerator generator) throws
            Exception{
        ContinueLineReader r = new ContinueLineReader(new LineNumberReader(new StringReader(queryText)));
        while (true) {
            String line = r.readLine();
            if (line == null) {
                break;
            }
            line = line.trim();

            if (line.startsWith("#") || line.length() == 0) {
                continue;
            }

            String lowercasedLine = line.toLowerCase();
            if (lowercasedLine.startsWith("select")
                    || lowercasedLine.startsWith("sql1") || lowercasedLine.startsWith("xpath")) {
                String language = "JCR-SQL2";
                if (lowercasedLine.startsWith("sql1 ")) {
                    language = "sql";
                    line = line.substring("sql1 ".length());
                } else if (lowercasedLine.startsWith("xpath ")) {
                    language = "xpath";
                    line = line.substring("xpath ".length());
                } else if (lowercasedLine.startsWith("/")){
                    language = "xpath";
                }
                generator.process(line, language);
            }
        }
    }

    /**
     * Taken from org.apache.jackrabbit.oak.query.AbstractQueryTest
     * A line reader that supports multi-line statements, where lines that start
     * with a space belong to the previous line.
     */
    private static class ContinueLineReader {

        private final LineNumberReader reader;

        ContinueLineReader(LineNumberReader reader) {
            this.reader = reader;
        }

        public String readLine() throws IOException {
            String line = reader.readLine();
            if (line == null || line.trim().length() == 0) {
                return line;
            }
            while (true) {
                reader.mark(4096);
                String next = reader.readLine();
                if (next == null || next.trim().length() == 0) {
                    reader.reset();
                    return line;
                }
                line = (line.trim() + "\n" + next).trim();
            }
        }
    }
}
