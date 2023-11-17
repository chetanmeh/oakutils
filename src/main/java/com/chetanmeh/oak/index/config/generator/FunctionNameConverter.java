package com.chetanmeh.oak.index.config.generator;

import java.util.List;

public class FunctionNameConverter {
  public final static List<String> functionNames = List.of("local-name", "name", "path", "upper-case", "lower-case",
    "coalesce", "first", "string-length");

  public static String apply(String functionPattern) {
    String functionName = extractFunctionName(functionPattern);
    String propertyName = extractPropertyName(functionPattern);
    propertyName = propertyName.substring(propertyName.indexOf(':') + 1);

    return toCamelCase(functionName, false) + toCamelCase(propertyName, true);
  }

  private static String extractFunctionName(String input) {
    return functionNames.stream().filter(input::contains).findFirst().orElse("");
  }

  private static String extractPropertyName(String input) {
    int start = input.indexOf('@');
    int end = input.indexOf(')');
    if (start != -1 && end != -1 && start < end) {
      return input.substring(start + 1, end);
    }
    return "";
  }

  private static String toCamelCase(String functionName, boolean capitalizeFirst) {
    StringBuilder result = new StringBuilder();
    boolean capitalize = capitalizeFirst;

    for (char c : functionName.toCharArray()) {
      if (c == ':' || c == '-') {
        capitalize = true;
      } else if (capitalize) {
        result.append(Character.toUpperCase(c));
        capitalize = false;
      } else {
        result.append(Character.toLowerCase(c));
      }
    }

    return result.toString();
  }

  public static void main(String[] args) {
    String example = "fn:lower-case(@jcr:title)";
    String converted = apply(example);
    System.out.println(converted); // Output: lowerCaseTitle

    String anotherExample = "fn:upper-case(@content:type)";
    String anotherConverted = apply(anotherExample);
    System.out.println(anotherConverted); // Output: upperCaseContentType

    String localNameExample = "fn:local-name(@jcr:data)";
    String localNameConverted = apply(localNameExample);
    System.out.println(localNameConverted); // Output: localNameData

    String jcrPath = "jcr:first(@jcr:data)";
    String jcrPathName = apply(jcrPath);
    System.out.println(jcrPathName); // Output: localNameData
  }
}
