package com.dbcat.gray.admin.dto;

import java.io.Serializable;

public class GrayConditionRequest implements Serializable {


    private String searchKey;

    private String type;

    private Integer enable;


    private Integer pageNum = 0;

    private Integer pageSize = 10;

    public String getSearchKey() {
        return searchKey;
    }

    public Integer getEnable() {
        return enable;
    }

    public void setEnable(Integer enable) {
        this.enable = enable;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
}