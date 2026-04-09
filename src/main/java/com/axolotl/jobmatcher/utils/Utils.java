package com.axolotl.jobmatcher.utils;

import com.axolotl.jobmatcher.exception.AppException;
import org.springframework.http.HttpStatus;

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

    public static void validatePaging(int offset, int limit){
        if (limit > 100 || limit < 0) {
            throw new AppException("Limit must be less than 100 and bigger than 0", HttpStatus.BAD_REQUEST);
        }
        if (offset < 0) {
            throw new AppException("Offset must not be less than 0", HttpStatus.BAD_REQUEST);
        }
        if (limit < 1){
            throw new AppException("Limit must not be less than 1", HttpStatus.BAD_REQUEST);
        }
    }
}
