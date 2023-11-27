package com.chetanmeh.oak.index.config.generator;

import org.junit.Test;

public class FunctionNameConverterTest {
    @Test
    public void testFormatNameSQL2() {
        checkConvert("function*upper*@data", "upperData", false);
        checkConvert("function*lower*@test/data", "lowerData", false);
        checkConvert("function*lower*@:name", "lowerName", false);
        checkConvert("function*lower*@:localname", "lowerLocalname", false);
        checkConvert("function*length*@test/data", "lengthData", false);
        checkConvert("function*length*@:name", "lengthName", false);
        checkConvert("function*@:path", "path", false);
        checkConvert("function*length*@:path", "lengthPath", false);
        checkConvert("function*lower*upper*@test/data", "lowerUpperData", false);
        checkConvert("function*coalesce*@jcr:content/foo2*@jcr:content/foo", "coalesceFoo2Foo", false);
        checkConvert("function*coalesce*@jcr:content/foo2*lower*@jcr:content/foo",
            "coalesceFoo2LowerFoo", false);
        checkConvert("function*coalesce*@jcr:content/foo2*coalesce*@jcr:content/foo*lower*@:name",
            "coalesceFoo2CoalesceFooLowerName", false);
        checkConvert(
            "function*coalesce*coalesce*@jcr:content/foo2*@jcr:content/foo*coalesce*@a:b*@c:d",
            "coalesceCoalesceFoo2FooCoalesceBD", false);
        checkConvert("function*first*@jcr:content/foo2", "firstFoo2", false);
    }

    @Test
    public void testFormatNameXPath() {
        checkConvert("function*upper*@data", "upperCaseData", true);
        checkConvert("function*lower*@test/data", "lowerCaseData", true);
        checkConvert("function*lower*@:name", "lowerCaseName", true);
        checkConvert("function*lower*@:localname", "lowerCaseLocalname", true);
        checkConvert("function*length*@test/data", "stringLengthData", true);
        checkConvert("function*length*@:name", "stringLengthName", true);
        checkConvert("function*@:path", "path", true);
        checkConvert("function*length*@:path", "stringLengthPath", true);
        checkConvert("function*lower*upper*@test/data", "lowerCaseUpperCaseData", true);
        checkConvert("function*coalesce*@jcr:content/foo2*@jcr:content/foo", "coalesceFoo2Foo", true);
        checkConvert("function*coalesce*@jcr:content/foo2*lower*@jcr:content/foo",
            "coalesceFoo2LowerCaseFoo", true);
        checkConvert("function*coalesce*@jcr:content/foo2*coalesce*@jcr:content/foo*lower*@:name",
            "coalesceFoo2CoalesceFooLowerCaseName", true);
        checkConvert(
            "function*coalesce*coalesce*@jcr:content/foo2*@jcr:content/foo*coalesce*@a:b*@c:d",
            "coalesceCoalesceFoo2FooCoalesceBD", true);
        checkConvert("function*first*@jcr:content/foo2", "firstFoo2", true);
    }


    private static void checkConvert(String input, String expected, boolean isXPath) {
        String actual = FunctionNameConverter.apply(input, isXPath);
        assert expected.equals(actual);
    }
}
