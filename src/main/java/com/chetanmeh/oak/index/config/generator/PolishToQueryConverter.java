package com.chetanmeh.oak.index.config.generator;

import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Set;

public class PolishToQueryConverter {

    /**
     * Converts a given Polish notation string to either XPath or JCR-SQL2 syntax based on the
     * specified flag.
     *
     * @param polishNotation The Polish notation string to be converted.
     * @param isXPath        A boolean flag indicating whether to convert to XPath (true) or
     *                       JCR-SQL2 (false) syntax.
     * @return A string representing the converted query in either XPath or JCR-SQL2 syntax.
     */
    public static String apply(String polishNotation, boolean isXPath) {
        Deque<String> tokens = new LinkedList<>(Arrays.asList(polishNotation.split("\\*")));
        if ("function".equals(tokens.peek())) {
            tokens.poll();
        }
        return parseTokens(tokens, isXPath);
    }

    /**
     * Recursively parses tokens from a deque representing a Polish notation expression and converts
     * them into either XPath or JCR-SQL2 query syntax. We use a deque, as we can tokenize each part
     * of the expression as they are separated by "*".
     *
     * @param tokens  A deque of tokens derived from the Polish notation expression.
     * @param isXPath A boolean flag indicating whether to convert to XPath (true) or JCR-SQL2
     *                (false) syntax.
     * @return A string representing the converted part of the query in the appropriate syntax.
     */
    private static String parseTokens(Deque<String> tokens, boolean isXPath) {
        if (tokens.isEmpty()) {
            return "";
        }

        String token = tokens.poll();
        String fn;

        return switch (token) {
            case "upper" -> {
                fn = isXPath ? "fn:upper-case(" : "upper(";
                yield fn + parseTokens(tokens, isXPath) + ")";
            }
            case "lower" -> {
                fn = isXPath ? "fn:lower-case(" : "lower(";
                yield fn + parseTokens(tokens, isXPath) + ")";
            }
            case "coalesce" -> {
                fn = isXPath ? "fn:coalesce(" : "coalesce(";
                yield fn + parseTokens(tokens, isXPath) + "," + parseTokens(tokens, isXPath) + ")";
            }
            case "first" -> {
                fn = isXPath ? "jcr:first(" : "first(";
                yield fn + parseTokens(tokens, isXPath) + ")";
            }
            case "length" -> {
                fn = isXPath ? "fn:string-length(" : "length(";
                yield fn + parseTokens(tokens, isXPath) + ")";
            }
            case "@:localname" -> isXPath ? "fn:local-name()" : "localname()";
            case "@:name" -> isXPath ? "fn:name()" : "name()";
            case "@:path" -> isXPath ? "fn:path()" : "path()";
            default ->
                // Handle properties
                isXPath ? formatXPathProperty(token) : formatSQL2Property(token);
        };
    }

    /*
       Properties in JCR-SQL2 needs to be surrounded with [ ] and doesn't have "@".
     */
    private static String formatSQL2Property(String token) {
        if (token.startsWith("@")) {
            return "[" + token.substring(1) + "]";
        }
        return token;
    }

    /**
     * This method formats properties from Polish notation to valid XPath. The property tokens are
     * always prefixed with "@". Since the token might contain a "/", meaning a nested property, we
     * need to format it to a valid XPath which means that the "deepest" child needs to be prefixed
     * with "@" instead. Example: "@jcr:content/foo/bar/property1" to
     * "jcr:content/foo/bar/@property1".
     *
     * @param token The property token in Polish notation.
     * @return The valid XPath formatted property.
     */
    private static String formatXPathProperty(String token) {
        if (token.contains("/")) {
            token = token.substring(token.indexOf("@") + 1);
            int lastSlash = token.lastIndexOf('/');
            return token.substring(0, lastSlash) + "/@" + token.substring(lastSlash + 1);
        }
        return token;
    }
}

