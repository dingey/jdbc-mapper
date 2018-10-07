package com.di.jdbc.mapper.core.impl;

import java.util.List;

import com.di.jdbc.mapper.core.Pager;

public class SimplePager<T> implements Pager<T> {
    private int pageNum;
    private int pageSize;
    private int total;
    List<T> list;

    SimplePager(int pageNum, int pageSize, int total, List<T> list) {
        super();
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.total = total;
        this.list = list;
    }

    @Override
    public int getPageNum() {
        return pageNum;
    }

    @Override
    public int getPageSize() {
        return pageSize;
    }

    @Override
    public int getTotal() {
        return total;
    }

    @Override
    public List<T> getList() {
        return list;
    }

}
