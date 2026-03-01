package com.dbcat.gray;

import com.dbcat.gray.filter.VersionComparator;
import org.junit.Test;

public class VersionComparatorTest {

    @Test
    public void testIsNumber() {
        long l = System.currentTimeMillis();
        for (int i = 0; i < 100000000; i++) {
            boolean abc123 = VersionComparator.isNumeric("12.34.5");
        }
        System.out.println("===" + (System.currentTimeMillis() - l));
    }

    @Test
    public void testIsNumber2() {
        long l = System.currentTimeMillis();
        for (int i = 0; i < 100000000; i++) {
            boolean abc123 = VersionComparator.isNumeric("abc123");
        }
        System.out.println("===2" + (System.currentTimeMillis() - l));
    }
}
