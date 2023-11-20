package com.chetanmeh.oak.index.config.generator;

import static com.chetanmeh.oak.index.config.generator.FunctionNameConverter.apply;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;

public class FunctionNameConverterTest {

    @Test
    public void testFormatName() {
        checkConvert("function*upper*@data", "upperData");
        checkConvert("function*lower*@test/data", "lowerData");
        checkConvert("function*lower*@:name", "lowerName");
        checkConvert("function*lower*@:localname", "lowerLocalname");
        checkConvert("function*length*@test/data", "lengthData");
        checkConvert("function*length*@:name", "lengthName");
        checkConvert("function*@:path", "path");
        checkConvert("function*length*@:path", "lengthPath");
        checkConvert("function*lower*upper*@test/data", "lowerUpperData");
        checkConvert("function*coalesce*@jcr:content/foo2*@jcr:content/foo", "coalesceFoo2Foo");
        checkConvert("function*coalesce*@jcr:content/foo2*lower*@jcr:content/foo",
            "coalesceFoo2LowerFoo");
        checkConvert("function*coalesce*@jcr:content/foo2*coalesce*@jcr:content/foo*lower*@:name",
            "coalesceFoo2CoalesceFooLowerName");
        checkConvert(
            "function*coalesce*coalesce*@jcr:content/foo2*@jcr:content/foo*coalesce*@a:b*@c:d",
            "coalesceCoalesceFoo2FooCoalesceBD");
        checkConvert("function*first*@jcr:content/foo2", "firstFoo2");
    }

    private static void checkConvert(String input, String expected) {
        String actual = FunctionNameConverter.apply(input);
        if (!actual.equals(expected)) {
            System.out.println("Expected: " + expected);
            System.out.println("Actual:   " + actual);
        }
        assertEquals(expected, actual);
    }
}
