package com.dbcat.gray.agent.core.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class InstanceEnvUpdateRequest implements Serializable {


    /**
     * 环境状态，1正常状态,0 灰度状态
     */
    private int envStatus;

    /**
     * strategy 0同步方式，1异步方式，2混合模式(快的用同步，慢的用异步)，3混合同步并发(快的同步，慢的并发同步)
     */
    private int strategy = 3;
}
