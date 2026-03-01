package com.dbcat.gray.admin.dto;


import lombok.Data;

@Data
public class AppVersionStatusUpdate {

    private String appName;


    private String version;

    private Integer envStatus;

}
