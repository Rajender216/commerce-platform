package com.rcp.commerce.catalog.adapter.in.web;

import com.rcp.commerce.catalog.application.port.in.PageResult;
import java.util.List;
import java.util.function.Function;

public record PageResponse<T>(
        List<T> content, int page, int size, long totalElements, int totalPages, boolean first, boolean last) {

    static <T, R> PageResponse<R> from(PageResult<T> page, Function<T, R> mapper) {
        return new PageResponse<>(
                page.content().stream().map(mapper).toList(),
                page.page(),
                page.size(),
                page.totalElements(),
                page.totalPages(),
                page.first(),
                page.last());
    }
}
