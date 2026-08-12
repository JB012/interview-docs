package com.interviewdocs.utils;

import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.Sort;

public class Utils {
    public Pageable getPageable(int page, int size, String field, String direction) {
        Pageable pageable = null;

        if (direction.trim().toLowerCase().equals("desc")) {
            pageable = Pageable.from(page, size, Sort.of(Sort.Order.desc(field)));
        }
        else if (direction.trim().toLowerCase().equals("asc")) {
            pageable = Pageable.from(page, size, Sort.of(Sort.Order.asc(field)));
        }

        return pageable;
    }
}
