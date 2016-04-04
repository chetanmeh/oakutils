package com.chetanmeh.oak.index.config.generator

import org.apache.jackrabbit.oak.query.QueryEngineImpl
import org.apache.jackrabbit.oak.spi.state.NodeState


class IndexConfigGeneratorHelper {

    static NodeState getIndexConfig(String queryText) {
        IndexConfigGenerator generator = new IndexConfigGenerator()
        extractQueriesAndGenerateIndex(queryText, generator)
        NodeState result = generator.getIndexConfig()
        return result
    }

    private static void extractQueriesAndGenerateIndex(String queryText, IndexConfigGenerator generator) {
        ContinueLineReader r = new ContinueLineReader(new LineNumberReader(new StringReader(queryText)));
        while (true) {
            String line = r.readLine();
            if (line == null) {
                break;
            }
            line = line.trim();

            if (line.startsWith("#") || line.length() == 0) {
                continue
            }

            if (line.startsWith("select")
                    || line.startsWith("sql1") || line.startsWith("xpath")) {
                String language = QueryEngineImpl.SQL2;
                if (line.startsWith("sql1 ")) {
                    language = QueryEngineImpl.SQL;
                    line = line.substring("sql1 ".length());
                } else if (line.startsWith("xpath ")) {
                    language = QueryEngineImpl.XPATH;
                    line = line.substring("xpath ".length());
                }
                generator.process(line, language)
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

        public void close() throws IOException {
            reader.close();
        }

        public String readLine() throws IOException {
            String line = reader.readLine();
            if (line == null || line.trim().length() == 0) {
                return line;
            }
            while (true) {
                reader.mark(4096);
                String next = reader.readLine();
                if (next == null || !next.startsWith(" ")) {
                    reader.reset();
                    return line;
                }
                line = (line.trim() + "\n" + next).trim();
            }
        }
    }
}
