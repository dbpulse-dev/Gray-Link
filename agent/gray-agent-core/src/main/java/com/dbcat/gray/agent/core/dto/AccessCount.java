package com.dbcat.gray.agent.core.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class AccessCount implements Serializable {

    private long accessTotalCount;

    private long accessGrayCount;

}
