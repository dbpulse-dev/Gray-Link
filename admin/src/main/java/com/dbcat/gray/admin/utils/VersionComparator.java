package com.dbcat.gray.admin.utils;


public class VersionComparator {

    public static boolean equals(String version1, String version2) {
        return compareVersions(version1, version2) == 0;
    }

    public static boolean less(String version1, String version2) {
        return compareVersions(version1, version2) < 0;
    }

    public static boolean greater(String version1, String version2) {
        return compareVersions(version1, version2) > 0;
    }

    public static int compareVersions(String version1, String version2) {
        String[] parts1 = version1.split("\\.");
        String[] parts2 = version2.split("\\.");
        int length = Math.max(parts1.length, parts2.length);
        for (int i = 0; i < length; i++) {
            int num1 = (i < parts1.length) ? Integer.parseInt(parts1[i]) : 0;
            int num2 = (i < parts2.length) ? Integer.parseInt(parts2[i]) : 0;
            if (num1 != num2) {
                return num1 - num2;
            }
        }
        return 0;
    }


    public static void main(String[] args) {
        String v = "3.13.2";
        String v2 = "3.13.12";
        final boolean less = less(v, v2);
        assert less;
    }

}