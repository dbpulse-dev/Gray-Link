package com.dbcat.gray.admin.dto.rabbit;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class RabbitClusterNode {

    @JsonProperty("cluster_links")
    private List<LinkNode> clusterLinks;
    @JsonProperty("disk_free")
    private long diskFree;

    public List<LinkNode> getClusterLinks() {
        return clusterLinks;
    }

    public void setClusterLinks(List<LinkNode> clusterLinks) {
        this.clusterLinks = clusterLinks;
    }

    public long getDiskFree() {
        return diskFree;
    }

    public void setDiskFree(long diskFree) {
        this.diskFree = diskFree;
    }
}
