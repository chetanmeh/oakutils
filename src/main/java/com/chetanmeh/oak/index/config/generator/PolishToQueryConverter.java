package com.chetanmeh.oak.index.config.generator;

import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class PolishToQueryConverter {
    public final static List<String> XPATH_FUNCTIONS = List.of("fn:local-name()", "fn:name()", "fn:path()", "fn:upper-case", "fn:lower-case", "fn:coalesce", "fn:string-length");
    public final static List<String> JCR_SQL2_FUNCTIONS = List.of("localname()", "name()", "path()", "upper", "lower", "coalesce", "first", "length");

    public static String apply(String polishNotation, boolean isXPath) {
        Deque<String> tokens = new LinkedList<>(Arrays.asList(polishNotation.split("\\*")));
        return parseTokens(tokens, isXPath);
    }

    private static String parseTokens(Deque<String> tokens, boolean isXPath) {
        if (tokens.isEmpty()) {
            return "";
        }

        String token = tokens.poll();
        if ("function".equals(token)) {
            return parseTokens(tokens, isXPath);
        }

        String fn;

        switch (token) {
            case "upper":
                fn = isXPath ? "fn:upper-case(" : "upper(";
                return fn + parseTokens(tokens, isXPath) + ")";
            case "lower":
                fn = isXPath ? "fn:lower-case(" : "lower(";
                return fn + parseTokens(tokens, isXPath) + ")";
            case "coalesce":
                fn = isXPath ? "fn:coalesce(" : "coalesce(";
                return fn + parseTokens(tokens, isXPath) + "," + parseTokens(tokens, isXPath) + ")";
            case "first":
                fn = isXPath ? "jcr:first(" : "first(";
                return fn + parseTokens(tokens, isXPath) + ")";
            case "length":
                fn = isXPath ? "fn:string-length(" : "length(";
                return fn + parseTokens(tokens, isXPath) + ")";
            case "@:localname":
                return isXPath ? "fn:local-name()" : "localname()";
            case "@:name":
                return isXPath ? "fn:name()" : "name()";
            case "@:path":
                return isXPath ? "fn:path()" : "path()";
            default:
                // Handle properties and other cases
                return isXPath ? formatXPathProperty(token) : formatSQL2Property(token);
        }
    }

    private static String formatSQL2Property(String token) {
        if (token.startsWith("@")) {
            // surround with [ ] and remove "@"
            return "[" + token.substring(1) + "]";
        }
        return token;
    }

    private static String formatXPathProperty(String token) {
        if (token.startsWith("@") && token.contains("/")) {
            token = token.substring(token.indexOf("@") + 1);
            int lastSlash = token.lastIndexOf('/');
            return token.substring(0, lastSlash) + "/@" + token.substring(lastSlash + 1);
        }
        return token;
    }
}

