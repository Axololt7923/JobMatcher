package com.axolotl.jobmatcher.utils;

import java.util.Collections;
import java.util.List;

public class Utils {
    public static <T> List<T> getResponsePage(List<T> sourceList, int offset, int limit) {
        if (sourceList == null || sourceList.isEmpty()) {
            return Collections.emptyList();
        }

        int totalSize = sourceList.size();

        int beginIdx = Math.min(offset, totalSize);

        int endIdx = Math.min(beginIdx + limit, totalSize);

        return sourceList.subList(beginIdx, endIdx);
    }
}
