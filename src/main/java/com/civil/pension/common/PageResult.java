package com.civil.pension.common;

public class PageResult<T> {

    private Long total;
    private Integer pageNum;
    private Integer pageSize;
    private T list;

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public T getList() {
        return list;
    }

    public void setList(T list) {
        this.list = list;
    }

    public static <T> PageResult<T> of(Long total, Integer pageNum, Integer pageSize, T list) {
        PageResult<T> result = new PageResult<>();
        result.setTotal(total);
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        result.setList(list);
        return result;
    }
}
