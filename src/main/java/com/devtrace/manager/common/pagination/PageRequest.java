package com.devtrace.manager.common.pagination;

public class PageRequest {

    private int page = 1;
    private int size = 20;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = Math.max(page, 1);
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = Math.max(size, 1);
    }

    public int getOffset() {
        return (page - 1) * size;
    }
}
