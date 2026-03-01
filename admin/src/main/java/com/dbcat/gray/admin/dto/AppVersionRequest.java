package com.dbcat.gray.admin.dto;


import lombok.Data;

@Data
public class AppVersionRequest {

    private String appName;

    private String searchKey;

    private String version;

}
