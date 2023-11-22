package com.chetanmeh.oak.index.config.generator;

import org.junit.Test;

public class PolishToQueryConverterTest {

    // taken from: https://github.com/apache/jackrabbit-oak/blob/trunk/oak-search/src/test/java/org/apache/jackrabbit/oak/plugins/index/search/util/FunctionIndexProcessorTest.java
    @Test
    public void testXPath() {
        checkConvert(
            "function*upper*@data",
            "fn:upper-case(@data)", true);
        checkConvert(
            "function*lower*@test/data",
            "fn:lower-case(test/@data)", true);
        checkConvert(
            "function*lower*@:name",
            "fn:lower-case(fn:name())", true);
        checkConvert(
            "function*lower*@:localname",
            "fn:lower-case(fn:local-name())", true);
        checkConvert(
            "function*length*@test/data",
            "fn:string-length(test/@data)", true);
        checkConvert(
            "function*length*@:name",
            "fn:string-length(fn:name())", true);
        checkConvert(
            "function*@:path",
            "fn:path()", true);
        checkConvert(
            "function*length*@:path",
            "fn:string-length(fn:path())", true);
        checkConvert(
            "function*length*@:path",
            "fn:string-length(@jcr:path)", true);
        checkConvert(
            "function*lower*upper*@test/data",
            "fn:lower-case(fn:upper-case(test/@data))", true);
        checkConvert(
            "function*coalesce*@jcr:content/foo2*@jcr:content/foo",
            "fn:coalesce(jcr:content/@foo2,jcr:content/@foo)", true);
        checkConvert("function*coalesce*@jcr:content/foo2*lower*@jcr:content/foo",
            "fn:coalesce(jcr:content/@foo2,fn:lower-case(jcr:content/@foo))", true);
        checkConvert("function*coalesce*@jcr:content/foo2*coalesce*@jcr:content/foo*lower*@:name",
            "fn:coalesce(jcr:content/@foo2,fn:coalesce(jcr:content/@foo,fn:lower-case(fn:name())))",
            true);
        checkConvert(
            "function*coalesce*coalesce*@jcr:content/foo2*@jcr:content/foo*coalesce*@a:b*@c:d",
            "fn:coalesce(fn:coalesce(jcr:content/@foo2,jcr:content/@foo),fn:coalesce(@a:b,@c:d))",
            true);
        checkConvert("function*first*@jcr:content/foo2",
            "jcr:first(jcr:content/@foo2)", true);
    }

    @Test
    public void testSql2() {
        checkConvert(
            "function*upper*@data",
            "upper([data])", false);
        checkConvert(
            "function*lower*@test/data",
            "lower([test/data])", false);
        checkConvert(
            "function*lower*@:name",
            "lower(name())", false);
        checkConvert(
            "function*lower*@:localname",
            "lower(localname())", false);
        checkConvert(
            "function*length*@test/data",
            "length([test/data])", false);
        checkConvert(
            "function*length*@:name",
            "length(name())", false);
        checkConvert(
            "function*@:path",
            "path()", false);
        checkConvert(
            "function*length*@:path",
            "length(path())", false);
        checkConvert(
            "function*length*@:path",
            "length([jcr:path])", false);
        checkConvert(
            "function*lower*upper*@test/data",
            "lower(upper([test/data]))", false);
        // the ']' character is escaped as ']]'
        checkConvert(
            "function*@strange[0]",
            "[strange[0]]]", false);
        checkConvert("function*coalesce*@jcr:content/foo2*@jcr:content/foo",
            "coalesce([jcr:content/foo2],[jcr:content/foo])", false);
        checkConvert("function*coalesce*@jcr:content/foo2*lower*@jcr:content/foo",
            "coalesce([jcr:content/foo2],lower([jcr:content/foo]))", false);
        checkConvert("function*coalesce*@jcr:content/foo2*coalesce*@jcr:content/foo*lower*@:name",
            "coalesce([jcr:content/foo2],coalesce([jcr:content/foo],lower(name())))", false);
        checkConvert(
            "function*coalesce*coalesce*@jcr:content/foo2*@jcr:content/foo*coalesce*@a:b*@c:d",
            "coalesce(coalesce([jcr:content/foo2],[jcr:content/foo]),coalesce([a:b],[c:d]))",
            false);
        checkConvert("function*first*@jcr:content/foo2",
            "first([jcr:content/foo2])", false);
    }

    private static void checkConvert(String input, String expected, boolean isXPath) {
        String res = PolishToQueryConverter.apply(input, isXPath);
        assert res.equals(expected);
    }
}
