package com.dbcat.gray.agent.core.dto;

import java.io.Serializable;
import java.util.List;

public class Cluster implements Serializable {

    private String md5;

    private List<Node> nodes;

    private Cluster grayCluster;

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }


    public Cluster getGrayCluster() {
        return grayCluster;
    }

    public void setGrayCluster(Cluster grayCluster) {
        this.grayCluster = grayCluster;
    }
}
