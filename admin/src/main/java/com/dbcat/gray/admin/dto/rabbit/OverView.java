package com.dbcat.gray.admin.dto.rabbit;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OverView {

    @JsonProperty("management_version")
    private String version;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
