package com.dbcat.gray.filter;

import com.dbcat.gray.filter.constant.Operator;

import java.util.List;

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

    public static boolean match(List<Operator> targetOps, String requestCondition, String targetCondition) {
        for (Operator op : targetOps) {
            if (op.equals(Operator.EQ) && VersionComparator.equals(requestCondition, targetCondition)) {
                return true;
            }
            if (op.equals(Operator.LT) && VersionComparator.less(requestCondition, targetCondition)) {
                return true;
            }
            if (op.equals(Operator.GT) && VersionComparator.greater(requestCondition, targetCondition)) {
                return true;
            }
        }
        return false;
    }


    public static boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        int startIndex = 0;
        // 检查开头的正负号
        if (str.charAt(0) == '-' || str.charAt(0) == '+') {
            startIndex = 1;
            if (str.length() == 1) {
                return false; // 只有符号，不算数字
            }
        }
        for (int i = startIndex; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '.') {
                continue;
            } else if (c < '0' || c > '9') {
                return false; // 非数字字符
            }
        }
        return true;
    }
}