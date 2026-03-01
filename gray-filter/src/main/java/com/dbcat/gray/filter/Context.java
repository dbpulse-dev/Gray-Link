package com.dbcat.gray.filter;

public interface Context {

    String getRequestHeader(String key);

    void setRequestHeader(String key, String value);

    String getCookie(String name);

    void setResponseHeader(String name, String value);

    Object getAttr(String key);

    void setAttr(String key, Object value);
}
