package me.hao0.antares.store.util;

import java.util.HashMap;
import java.util.Map;

/**
 * The paging util
 */
public class Paging<T> {

    private Integer offset;

    private Integer limit;

    public Paging() {
        this(1, 20);
    }

    public Paging(Integer pageNo, Integer pageSize) {
        this(pageNo, pageSize, Integer.MAX_VALUE);
    }

    public Paging(Integer pageNo, Integer pageSize, Integer maxPageSize) {

        pageNo = pageNo == null || pageNo < 0 ? 1 : pageNo;

        pageSize = pageSize == null || pageSize < 0 ? 20 : pageSize;
        pageSize = pageSize > maxPageSize ? maxPageSize : pageSize;

        limit = pageSize > 0 ? pageSize : 20;
        offset = (pageNo - 1) * pageSize;
    }

    public static Paging of(Integer pageNo, Integer pageSize) {
        return new Paging(pageNo, pageSize);
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Map<String, Integer> toMap() {
        Map<String, Integer> map = new HashMap<>();
        map.put("offset", offset);
        map.put("limit", limit);
        return map;
    }

}