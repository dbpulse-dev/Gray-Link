package com.dbcat.gray.agent.core.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class InstancePullRequest implements Serializable {
    private String appName;
    private String ip;
}
