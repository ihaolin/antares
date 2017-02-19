package me.hao0.antares.store.util;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * The page util
 */
public class Page<T> implements Serializable {

    private static final long serialVersionUID = 7544274721272147458L;

    private Long total;

    private List<T> data;

    public Page(Long total, List<T> data){
        this.total = total;
        this.data = data;
    }

    public static <T> Page<T> empty() {
        return new Page<T>(0L, Collections.<T>emptyList());
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Page{" +
                "total=" + total +
                ", data=" + data +
                '}';
    }
}