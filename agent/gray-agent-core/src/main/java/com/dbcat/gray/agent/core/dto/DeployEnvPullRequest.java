package com.dbcat.gray.agent.core.dto;

import lombok.Data;

@Data
public class DeployEnvPullRequest {
    private String appName;
    private String appVersion;
}
