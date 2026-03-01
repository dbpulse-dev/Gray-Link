package com.dbcat.gray.admin.dto.rabbit;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class RabbitVirtualHost {

    private String name;

    @JsonProperty("description")
    private String description;

    /**
     * 标签
     */
    @JsonProperty("tags")
    private List<String> tags;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
