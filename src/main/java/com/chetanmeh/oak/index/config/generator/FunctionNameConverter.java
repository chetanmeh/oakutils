package com.chetanmeh.oak.index.config.generator;

import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;

public class FunctionNameConverter {

    // Map from function to node name (name of the node in the index definition)
    private final static Map<String, String> XPATH_NAMES = Map.of(
        "upper", "upperCase",
        "lower", "lowerCase",
        "coalesce", "coalesce",
        "first", "first",
        "length", "stringLength",
        "@:localname", "localname",
        "@:name", "name",
        "@:path", "path"
    );

    /**
     * Converts a given function pattern in polish notation into a string in camelCase. This is used
     * to generate node names from the query. For example, the function pattern
     * "function*upper*@data" will be converted to "upperData" if the query was written in JCR-SQL2
     * and to "upperCaseData" if the query was written in XPath.
     *
     * @param functionPattern The string pattern representing a function. It is split into tokens
     *                        based on the '*' character.
     * @return A string combining the function name(s) and properties in camelCase.
     */
    public static String apply(String functionPattern, boolean isXPath) {
        Deque<String> tokens = new LinkedList<>(Arrays.asList(functionPattern.split("\\*")));
        if ("function".equals(tokens.peek())) {
            tokens.poll();
        }

        String converted = parse(tokens, isXPath);

        // lowercase the first letter
        return converted.substring(0, 1).toLowerCase(Locale.ENGLISH) + converted.substring(1);
    }

    private static String parse(Deque<String> tokens, boolean isXPath) {
        if (tokens.isEmpty()) {
            return "";
        }

        String token = tokens.poll();
        String fn;

        return switch (token) {
            // All function names are capitalized as we want the node name to be camelCase. The only
            // exception is the starting function. However, in this function, we "naively"
            // capitalize all functions and handle the exception the in the apply method as this is
            // easier.
            case "upper", "lower", "first", "length", "@:localname", "@:name", "@:path" -> {
                fn = isXPath ? capitalize(XPATH_NAMES.get(token)) : capitalize(token);
                yield fn + parse(tokens, isXPath);
            }
            case "coalesce" -> capitalize(token) + parse(tokens, isXPath) + parse(tokens, isXPath);
            default -> capitalize(extractPropertyName(token));
        };
    }

    /**
     * Capitalizes the first letter of the given string. If the string starts with a special prefix
     * "@:", this prefix is removed before capitalization. If the string is null or empty, it is
     * returned as is.
     *
     * @param str The string to be capitalized.
     * @return The capitalized string, or as is if it is null or empty.
     */
    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        // Remove "@:" prefix if present
        if (str.startsWith("@:")) {
            str = str.substring(2);
        }

        return str.substring(0, 1).toUpperCase(Locale.ENGLISH) + str.substring(1);
    }

    /**
     * Extracts the property name from the string. A property name is assumed to start with a '@'.
     * If that is the case and the string contains characters like ':' and/or '/' we need to handle
     * that. For example:
     * <p>
     * "@jcr:content/foo2" -> "foo2"
     *
     * @param input The input string containing the property name.
     * @return The extracted property name.
     */
    private static String extractPropertyName(String input) {
        if (input.contains("/")) {
            int slash = input.lastIndexOf("/");
            return input.charAt(slash + 1) + input.substring(slash + 2);
        }

        if (input.contains(":")) {
            int colon = input.lastIndexOf(":");
            return input.charAt(colon + 1) + input.substring(colon + 2);
        }

        return input.substring(1);
    }
}
